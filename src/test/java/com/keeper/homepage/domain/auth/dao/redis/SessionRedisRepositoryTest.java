package com.keeper.homepage.domain.auth.dao.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keeper.homepage.global.config.security.session.SessionAuthenticationFactory;
import com.keeper.homepage.global.config.security.session.SessionLookupResult;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.GrantedAuthority;

/** Run against an isolated Redis 7 server with SESSION_TEST_REDIS_PORT set. */
@EnabledIfEnvironmentVariable(named = "SESSION_TEST_REDIS_PORT", matches = "[0-9]+")
class SessionRedisRepositoryTest {

  private static LettuceConnectionFactory connectionFactory;
  private static StringRedisTemplate redis;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final List<String> keys = new ArrayList<>();
  private final long memberId = ThreadLocalRandom.current().nextLong(1, 1L << 40);
  private SessionRedisRepository repository;

  @BeforeAll
  static void connect() {
    int port = Integer.parseInt(System.getenv("SESSION_TEST_REDIS_PORT"));
    connectionFactory = new LettuceConnectionFactory("127.0.0.1", port);
    connectionFactory.afterPropertiesSet();
    redis = new StringRedisTemplate(connectionFactory);
  }

  @BeforeEach
  void setUp() {
    repository = new SessionRedisRepository(redis, objectMapper);
  }

  @AfterEach
  void removeTestKeys() {
    if (!keys.isEmpty()) {
      redis.delete(keys);
    }
  }

  @AfterAll
  static void disconnect() {
    connectionFactory.destroy();
  }

  @Test
  void updatesEveryMatchingSessionAndPreservesExpiryAndOtherFields() throws Exception {
    String first = createSession(memberId);
    String second = createSession(memberId);
    String other = createSession(memberId + 1);
    String otherValue = redis.opsForValue().get(other);
    long firstExpiry = expiresAt(first);
    long secondExpiry = expiresAt(second);
    ObjectNode expected = (ObjectNode) objectMapper.readTree(redis.opsForValue().get(first));
    expected.set("roles", objectMapper.valueToTree(List.of("ROLE_회원", "ROLE_회장")));

    assertThat(repository.updateAllSessionRoles(memberId, List.of("ROLE_회원", "ROLE_회장")))
        .isEqualTo(2);

    assertThat(objectMapper.readTree(redis.opsForValue().get(first))).isEqualTo(expected);
    assertThat(objectMapper.readTree(redis.opsForValue().get(second)).get("roles"))
        .isEqualTo(expected.get("roles"));
    assertThat(expiresAt(first)).isEqualTo(firstExpiry);
    assertThat(expiresAt(second)).isEqualTo(secondExpiry);
    assertThat(redis.opsForValue().get(other)).isEqualTo(otherValue);

    SessionLookupResult result = repository.findAndTouch(first,
        Duration.ofMinutes(5).toMillis(), Duration.ofMinutes(1).toMillis());
    assertThat(result.isValid()).isTrue();
    assertThat(result.session().roles()).containsExactly("ROLE_회원", "ROLE_회장");
    assertThat(result.touched()).isFalse();
    assertThat(new SessionAuthenticationFactory().create(result.session()).getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_회원", "ROLE_회장");

    repository.updateAllSessionRoles(memberId, List.of("ROLE_회원"));
    assertThat(objectMapper.readTree(redis.opsForValue().get(first)).get("roles"))
        .isEqualTo(objectMapper.valueToTree(List.of("ROLE_회원")));
    assertThat(expiresAt(first)).isEqualTo(firstExpiry);
    SessionLookupResult downgraded = repository.findAndTouch(first,
        Duration.ofMinutes(5).toMillis(), Duration.ofMinutes(1).toMillis());
    assertThat(downgraded.session().roles()).containsExactly("ROLE_회원");
  }

  @Test
  void refreshesIdleExpiryForSessionWithUpdatedRoles() throws Exception {
    String key = createSession(memberId);
    List<String> roles = List.of("ROLE_회원", "ROLE_회장");
    repository.updateAllSessionRoles(memberId, roles);
    redis.expire(key, Duration.ofSeconds(30));
    long previousExpiry = expiresAt(key);

    SessionLookupResult result = repository.findAndTouch(key,
        Duration.ofMinutes(5).toMillis(), Duration.ofMinutes(1).toMillis());

    assertThat(result.isValid()).isTrue();
    assertThat(result.touched()).isTrue();
    assertThat(result.session().roles()).containsExactlyElementsOf(roles);
    assertThat(result.expiresAt()).isGreaterThan(previousExpiry);
    assertThat(expiresAt(key)).isEqualTo(result.expiresAt());
    assertThat(objectMapper.readTree(redis.opsForValue().get(key)).get("roles"))
        .isEqualTo(objectMapper.valueToTree(roles));
  }

  @Test
  void deletesAllSessionsForOneMemberAndCanBeRepeated() {
    String first = createSession(memberId);
    String second = createSession(memberId);
    String other = createSession(memberId + 1);

    assertThat(repository.deleteAllSessions(memberId)).isEqualTo(2);
    assertThat(redis.hasKey(first)).isFalse();
    assertThat(redis.hasKey(second)).isFalse();
    assertThat(redis.hasKey(other)).isTrue();
    assertThat(repository.deleteAllSessions(memberId)).isZero();
    assertThat(repository.updateAllSessionRoles(memberId, List.of("ROLE_회원"))).isZero();
    assertThat(redis.hasKey(first)).isFalse();
    assertThat(redis.hasKey(second)).isFalse();
  }

  @Test
  void malformedEntriesAndOtherKeyTypesDoNotPreventProcessingValidSessions() {
    String malformed = newKey("session:");
    redis.opsForValue().set(malformed, "not-json");
    String jsonNull = newKey("session:");
    redis.opsForValue().set(jsonNull, "null");
    String wrongType = newKey("session:");
    redis.opsForSet().add(wrongType, "value");
    String valid = createSession(memberId);
    String unrelated = newKey("email:");
    redis.opsForValue().set(unrelated, redis.opsForValue().get(valid));

    assertThat(repository.updateAllSessionRoles(memberId, List.of("ROLE_회원", "ROLE_회장")))
        .isEqualTo(1);
    assertThat(repository.deleteAllSessions(memberId)).isEqualTo(1);
    assertThat(redis.hasKey(valid)).isFalse();
    assertThat(redis.opsForValue().get(malformed)).isEqualTo("not-json");
    assertThat(redis.opsForValue().get(jsonNull)).isEqualTo("null");
    assertThat(redis.opsForSet().members(wrongType)).containsExactly("value");
    assertThat(redis.hasKey(unrelated)).isTrue();
  }

  @Test
  void doesNotUpdateSessionsWithoutExpiryOrPastAbsoluteExpiry() throws Exception {
    String noExpiry = createSession(memberId);
    redis.persist(noExpiry);
    String expired = createSession(memberId);
    ObjectNode expiredValue = (ObjectNode) objectMapper.readTree(redis.opsForValue().get(expired));
    expiredValue.put("absolute_expires_at", 1L);
    redis.opsForValue().set(expired, expiredValue.toString(), Duration.ofMinutes(1));
    String missing = createSession(memberId);
    redis.delete(missing);
    String original = redis.opsForValue().get(noExpiry);

    assertThat(repository.updateAllSessionRoles(memberId, List.of("ROLE_회장"))).isZero();
    assertThat(redis.opsForValue().get(noExpiry)).isEqualTo(original);
    assertThat(redis.opsForValue().get(expired)).isEqualTo(expiredValue.toString());
    assertThat(redis.hasKey(missing)).isFalse();
    assertThat(repository.deleteAllSessions(memberId)).isEqualTo(2);
  }

  @Test
  void rejectsInvalidMemberIdsBeforeChangingSessions() {
    String key = createSession(memberId);
    String original = redis.opsForValue().get(key);

    assertThatThrownBy(() -> repository.updateAllSessionRoles(0, List.of("ROLE_회원")))
        .isInstanceOf(RuntimeException.class);
    assertThatThrownBy(() -> repository.deleteAllSessions(0))
        .isInstanceOf(RuntimeException.class);
    assertThat(redis.opsForValue().get(key)).isEqualTo(original);
  }

  private String createSession(long userId) {
    String key = newKey("session:");
    repository.create(key, userId, List.of("ROLE_회원"),
        Duration.ofMinutes(5).toMillis(), Duration.ofDays(30).toMillis());
    return key;
  }

  private String newKey(String prefix) {
    String key = prefix + UUID.randomUUID();
    keys.add(key);
    return key;
  }

  private long expiresAt(String key) {
    return redis.execute(new DefaultRedisScript<>(
        "return redis.call('PEXPIRETIME', KEYS[1])", Long.class), List.of(key));
  }
}

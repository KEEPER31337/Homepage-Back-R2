package com.keeper.homepage.domain.auth.dao.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeper.homepage.global.config.security.session.SessionLookupResult;
import com.keeper.homepage.global.config.security.session.SessionStoreException;
import com.keeper.homepage.global.config.security.session.StoredSessionCreationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SessionRedisRepository {

  private static final DefaultRedisScript<String> CREATE_SCRIPT = script(
      "redis/session/create.lua");
  private static final DefaultRedisScript<String> FIND_AND_TOUCH_SCRIPT = script(
      "redis/session/find-and-touch.lua");

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public StoredSessionCreationResult create(String key, long userId, List<String> roles,
      long idleTimeoutMillis, long absoluteTimeoutMillis) {
    try {
      String rolesJson = objectMapper.writeValueAsString(roles);
      String result = redisTemplate.execute(CREATE_SCRIPT, List.of(key),
          Long.toString(userId),
          Long.toString(idleTimeoutMillis),
          Long.toString(absoluteTimeoutMillis),
          rolesJson);
      return readResult(result, StoredSessionCreationResult.class);
    } catch (JsonProcessingException e) {
      throw new SessionStoreException("세션 역할을 직렬화하지 못했습니다.", e);
    }
  }

  public SessionLookupResult findAndTouch(String key, long idleTimeoutMillis,
      long touchThresholdMillis) {
    String result = redisTemplate.execute(FIND_AND_TOUCH_SCRIPT, List.of(key),
        Long.toString(idleTimeoutMillis),
        Long.toString(touchThresholdMillis));
    return readResult(result, SessionLookupResult.class);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  private <T> T readResult(String result, Class<T> type) {
    if (result == null) {
      throw new SessionStoreException("Redis 세션 스크립트가 결과를 반환하지 않았습니다.");
    }
    try {
      return objectMapper.readValue(result, type);
    } catch (JsonProcessingException e) {
      throw new SessionStoreException("Redis 세션 스크립트 결과를 읽지 못했습니다.", e);
    }
  }

  private static DefaultRedisScript<String> script(String path) {
    DefaultRedisScript<String> script = new DefaultRedisScript<>();
    script.setLocation(new ClassPathResource(path));
    script.setResultType(String.class);
    return script;
  }
}

package com.keeper.homepage.global.util.redis;


import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.DAYS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisUtil {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public <T> Optional<T> getData(String key, Class<T> classType) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    String value = valueOperations.get(key);
    if (value == null) {
      return Optional.empty();
    }
    try {
      return Optional.ofNullable(objectMapper.readValue(value, classType));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> void setDataExpire(String key, T value, long durationMillis) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    Duration expireDuration = Duration.ofMillis(durationMillis);
    try {
      valueOperations.set(key, objectMapper.writeValueAsString(value), expireDuration);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Long increaseAndGetWithExpire(String key, long durationMillis) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    Duration expireDuration = Duration.ofMillis(durationMillis);

    long incrementedValue = valueOperations.increment(key);
    try {
      valueOperations.set(key, objectMapper.writeValueAsString(incrementedValue), expireDuration);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return incrementedValue;
  }

  public static long toMidNight() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime midnight = now.plusDays(1).truncatedTo(DAYS);
    long secondsUntilMidnight = midnight.toEpochSecond(UTC) - now.toEpochSecond(UTC);
    return secondsUntilMidnight * 1000;
  }

  public void deleteData(String key) {
    redisTemplate.delete(key);
  }

  public void flushAll() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }
}

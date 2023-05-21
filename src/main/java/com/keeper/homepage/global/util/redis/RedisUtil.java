package com.keeper.homepage.global.util.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
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

  public void deleteData(String key) {
    redisTemplate.delete(key);
  }

  public void flushAll() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }
}

package com.keeper.homepage.global.util.redis;


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

  public Optional<String> getData(String key) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    return Optional.ofNullable(valueOperations.get(key));
  }

  public void setDataExpire(String key, String value, long durationMillis) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    Duration expireDuration = Duration.ofMillis(durationMillis);
    valueOperations.set(key, value, expireDuration);
  }

  public void deleteData(String key) {
    redisTemplate.delete(key);
  }
}

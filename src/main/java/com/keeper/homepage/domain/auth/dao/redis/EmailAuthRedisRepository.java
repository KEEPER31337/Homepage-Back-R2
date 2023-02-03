package com.keeper.homepage.domain.auth.dao.redis;

import com.keeper.homepage.domain.auth.entity.redis.EmailAuthRedis;
import org.springframework.data.repository.CrudRepository;

public interface EmailAuthRedisRepository extends CrudRepository<EmailAuthRedis, String> {

}

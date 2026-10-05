package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.auth.dao.redis.SessionRedisRepository;
import com.keeper.homepage.global.config.security.session.SessionCreationResult;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import com.keeper.homepage.global.config.security.session.SessionIdGenerator;
import com.keeper.homepage.global.config.security.session.SessionLookupResult;
import com.keeper.homepage.global.config.security.session.SessionPolicy;
import com.keeper.homepage.global.config.security.session.SessionStoreException;
import com.keeper.homepage.global.config.security.session.StoredSessionCreationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

  private static final int MAX_CREATE_ATTEMPTS = 3;

  private final SessionIdGenerator sessionIdGenerator;
  private final SessionIdCodec sessionIdCodec;
  private final SessionRedisRepository sessionRedisRepository;

  public SessionCreationResult createSession(long userId, List<String> roles) {
    if (userId <= 0) {
      throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
    }
    if (roles.isEmpty()) {
      throw new IllegalArgumentException("올바르지 않은 역할 정보입니다.");
    }

    List<String> copiedRoles = List.copyOf(roles);
    for (int attempt = 0; attempt < MAX_CREATE_ATTEMPTS; attempt++) {
      String sessionId = sessionIdGenerator.generate();
      String key = sessionIdCodec.toRedisKey(sessionId).orElseThrow();
      StoredSessionCreationResult result = sessionRedisRepository.create(
          key,
          userId,
          copiedRoles,
          SessionPolicy.IDLE_TIMEOUT.toMillis(),
          SessionPolicy.ABSOLUTE_TIMEOUT.toMillis());
      if (result.status() == StoredSessionCreationResult.Status.CREATED) {
        return new SessionCreationResult(sessionId, result.session(), result.expiresAt());
      }
    }
    throw new SessionStoreException("고유한 세션 ID를 생성하지 못했습니다.");
  }

  public SessionLookupResult findAndTouch(String sessionId) {
    return sessionIdCodec.toRedisKey(sessionId)
        .map(key -> sessionRedisRepository.findAndTouch(
            key,
            SessionPolicy.IDLE_TIMEOUT.toMillis(),
            SessionPolicy.TOUCH_THRESHOLD.toMillis()))
        .orElseGet(SessionLookupResult::invalid);
  }

  public void deleteSession(String sessionId) {
    sessionIdCodec.toRedisKey(sessionId).ifPresent(sessionRedisRepository::delete);
  }

  public void updateAllSessionRoles(long userId, List<String> roles) {
    if (userId <= 0) {
      throw new IllegalArgumentException("올바르지 않은 회원 정보입니다.");
    }
    if (roles.isEmpty()) {
      throw new IllegalArgumentException("올바르지 않은 역할 정보입니다.");
    }

    List<String> copiedRoles = List.copyOf(roles);
    afterCommit(() -> {
      try {
        sessionRedisRepository.updateAllSessionRoles(userId, copiedRoles);
      } catch (RuntimeException e) {
        log.error("Redis 세션 반영 실패: operation=updateAllSessionRoles, userId={}", userId, e);
      }
    });
  }

  public void deleteAllSessions(long userId) {
    if (userId <= 0) {
      throw new IllegalArgumentException("올바르지 않은 회원 ID입니다.");
    }
    afterCommit(() -> {
      try {
        sessionRedisRepository.deleteAllSessions(userId);
      } catch (RuntimeException e) {
        log.error("Redis 세션 반영 실패: operation=deleteAllSessions, userId={}", userId, e);
      }
    });
  }

  private static void afterCommit(Runnable action) {
    if (!TransactionSynchronizationManager.isActualTransactionActive()) {
      action.run();
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        action.run();
      }
    });
  }
}

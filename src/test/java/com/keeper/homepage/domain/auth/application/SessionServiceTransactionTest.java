package com.keeper.homepage.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.keeper.homepage.domain.auth.dao.redis.SessionRedisRepository;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import com.keeper.homepage.global.config.security.session.SessionIdGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(OutputCaptureExtension.class)
class SessionServiceTransactionTest {

  private final SessionRedisRepository repository = mock(SessionRedisRepository.class);
  private final SessionService service = new SessionService(
      new SessionIdGenerator(), new SessionIdCodec(), repository);
  private final TestTransactionManager transactionManager = new TestTransactionManager();
  private final TransactionTemplate transaction = new TransactionTemplate(transactionManager);

  @Test
  void updatesRoleSnapshotOnlyAfterCommit() {
    List<String> roles = new ArrayList<>(List.of("ROLE_회원", "ROLE_회장"));

    transaction.executeWithoutResult(status -> {
      service.updateAllSessionRoles(123L, roles);
      roles.clear();
      verifyNoInteractions(repository);
    });

    verify(repository).updateAllSessionRoles(123L, List.of("ROLE_회원", "ROLE_회장"));
  }

  @Test
  void rejectsEmptyRolesBeforeSchedulingAnUpdate() {
    transaction.executeWithoutResult(status -> {
      assertThatThrownBy(() -> service.updateAllSessionRoles(123L, List.of()))
          .isInstanceOf(IllegalArgumentException.class);
      verifyNoInteractions(repository);
    });

    verifyNoInteractions(repository);
  }

  @Test
  void deletesAllSessionsOnlyAfterCommit() {
    transaction.executeWithoutResult(status -> {
      service.deleteAllSessions(123L);
      verifyNoInteractions(repository);
    });

    verify(repository).deleteAllSessions(123L);
  }

  @Test
  void rollbackLeavesSessionsUnchanged() {
    transaction.executeWithoutResult(status -> {
      service.updateAllSessionRoles(123L, List.of("ROLE_회원"));
      service.deleteAllSessions(456L);
      status.setRollbackOnly();
    });

    verifyNoInteractions(repository);
  }

  @Test
  void appliesChangesImmediatelyWithoutTransaction() {
    service.updateAllSessionRoles(123L, List.of("ROLE_회원"));
    service.deleteAllSessions(456L);

    verify(repository).updateAllSessionRoles(123L, List.of("ROLE_회원"));
    verify(repository).deleteAllSessions(456L);
  }

  @Test
  void rejectsInvalidMemberIdsAndEmptyRolesBeforeAccessingRedis() {
    assertThatThrownBy(() -> service.deleteAllSessions(0))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.updateAllSessionRoles(-1, List.of("ROLE_회원")))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.updateAllSessionRoles(123L, List.of()))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.createSession(123L, List.of()))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(repository);
  }

  @Test
  void logsFailedDeletionAndContinuesWithRemainingMembers(CapturedOutput output) {
    var failure = new DataAccessResourceFailureException("Redis unavailable");
    doThrow(failure).when(repository).deleteAllSessions(123L);

    transaction.executeWithoutResult(status -> {
      service.deleteAllSessions(123L);
      service.deleteAllSessions(456L);
    });

    assertThat(transactionManager.committed).isTrue();
    verify(repository).deleteAllSessions(123L);
    verify(repository).deleteAllSessions(456L);
    assertThat(output.getAll())
        .contains("operation=deleteAllSessions", "userId=123", "Redis unavailable");
  }

  @Test
  void logsFailedRoleUpdateAndContinuesWithRemainingCallbacks(CapturedOutput output) {
    var failure = new DataAccessResourceFailureException("Redis unavailable");
    doThrow(failure).when(repository).updateAllSessionRoles(123L, List.of("ROLE_회원"));

    transaction.executeWithoutResult(status -> {
      service.updateAllSessionRoles(123L, List.of("ROLE_회원"));
      service.deleteAllSessions(456L);
    });

    assertThat(transactionManager.committed).isTrue();
    verify(repository).updateAllSessionRoles(123L, List.of("ROLE_회원"));
    verify(repository).deleteAllSessions(456L);
    assertThat(output.getAll())
        .contains("operation=updateAllSessionRoles", "userId=123", "Redis unavailable");
  }

  private static class TestTransactionManager extends AbstractPlatformTransactionManager {

    private boolean committed;

    @Override
    protected Object doGetTransaction() {
      return new Object();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
      committed = true;
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
    }
  }
}

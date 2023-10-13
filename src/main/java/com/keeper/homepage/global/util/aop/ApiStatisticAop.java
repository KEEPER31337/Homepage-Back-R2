package com.keeper.homepage.global.util.aop;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiStatisticAop {

  private final ApiStatistic apiStatistic;

  /* TODO: Spring 스케줄러 메서드 실행이 안되서 주석 처리.
  @Around("execution(* javax.sql.DataSource.getConnection())")
  public Object getConnection(ProceedingJoinPoint joinPoint) throws Throwable {
    Object connection = joinPoint.proceed();
    return Proxy.newProxyInstance(
        connection.getClass().getClassLoader(),
        connection.getClass().getInterfaces(),
        new ConnectionProxyHandler(connection, apiStatistic)
    );
  }*/

  @Around("within(@org.springframework.web.bind.annotation.RestController *)")
  public Object calculateExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (isInRequestScope(attributes)) {
      apiStatistic.updateApiUrl(attributes.getRequest().getRequestURI());
    }

    final Long beforeTime = System.currentTimeMillis();

    Object result = joinPoint.proceed();

    final Long afterTime = System.currentTimeMillis();
    apiStatistic.updateTotalTime(afterTime - beforeTime);
    log.info("api 통계 : URL = {}, API 실행 시간 = {}(ms), 쿼리 호출 횟수 = {}번, 쿼리 실행 시간 = {}(ms)",
        apiStatistic.getApiUrl(), apiStatistic.getTotalTime(), apiStatistic.getQueryCounts(),
        apiStatistic.getQueryTime());
    return result;
  }

  private boolean isInRequestScope(final ServletRequestAttributes attributes) {
    return Objects.nonNull(attributes);
  }
}

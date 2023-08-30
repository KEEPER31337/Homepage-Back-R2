package com.keeper.homepage.global.util.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApiQueryCounterAop {

  @Around("within(@org.springframework.web.bind.annotation.RestController *)")
  public Object calculateExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
    final Long beforeTime = System.currentTimeMillis();

    Object result=joinPoint.proceed();

    final Long afterTime = System.currentTimeMillis();
    final Double secDiffTime = ((double) (afterTime - beforeTime) / 1000);
    log.info("총 소요 시간(s) : {}", secDiffTime);
    return result;
  }
}

package com.keeper.homepage.global.util.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PreparedStatementProxyHandler implements
    InvocationHandler {

  private static final List<String> JDBC_QUERY_METHOD =
      List.of("executeQuery", "execute", "executeUpdate");

  private final Object preparedStatement;
  private final ApiStatistic apiStatistic;

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    if (isExecuteQuery(method)) {
      final Long beforeTime = System.currentTimeMillis();
      final Object result = method.invoke(preparedStatement, args);
      final Long afterTime = System.currentTimeMillis();
      apiStatistic.queryCountUp();
      apiStatistic.addQueryTime(afterTime - beforeTime);

      return result;
    }
    return method.invoke(preparedStatement, args);
  }

  private boolean isExecuteQuery(final Method method) {
    return JDBC_QUERY_METHOD.contains(method.getName());
  }
}

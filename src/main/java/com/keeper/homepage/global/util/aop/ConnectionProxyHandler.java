package com.keeper.homepage.global.util.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionProxyHandler implements InvocationHandler {

  private static final String JDBC_PREPARE_STATEMENT_METHOD_NAME = "prepareStatement";

  private final Object connection;
  private final ApiStatistic apiStatistic;

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    Object invokeResult = method.invoke(connection, args);
    if (isGeneratePrepareStatement(method)) {
      return Proxy.newProxyInstance(
          invokeResult.getClass().getClassLoader(),
          invokeResult.getClass().getInterfaces(),
          new PreparedStatementProxyHandler(invokeResult, apiStatistic)
      );
    }
    return invokeResult;
  }

  private boolean isGeneratePrepareStatement(final Method method) {
    return JDBC_PREPARE_STATEMENT_METHOD_NAME.equals(method.getName());
  }
}

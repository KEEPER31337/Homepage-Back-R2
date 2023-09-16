package com.keeper.homepage.global.util.aop;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@ToString
public class ApiStatistic {

  private String apiUrl;
  private Long totalTime = 0L;
  private Long queryCounts = 0L;
  private Long queryTime = 0L;

  public void queryCountUp() {
    queryCounts++;
  }

  public void addQueryTime(final Long queryTime) {
    this.queryTime += queryTime;
  }

  public void updateApiUrl(final String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public void updateTotalTime(final Long totalTime) {
    this.totalTime = totalTime;
  }
}

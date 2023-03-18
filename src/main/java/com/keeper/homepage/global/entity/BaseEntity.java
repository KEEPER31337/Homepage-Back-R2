package com.keeper.homepage.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

  @CreatedDate
  @Column(name = "register_time", nullable = false, updatable = false)
  private LocalDateTime registerTime;

  @LastModifiedDate
  @Column(name = "update_time", nullable = false)
  private LocalDateTime updateTime;

  public LocalDateTime getRegisterTime() {
    return registerTime;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }
}

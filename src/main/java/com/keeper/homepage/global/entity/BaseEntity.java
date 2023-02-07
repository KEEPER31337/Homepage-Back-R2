package com.keeper.homepage.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

  @Column(name = "register_time", nullable = false, updatable = false)
  private LocalDateTime registerTime;

  @Column(name = "update_time", nullable = false, updatable = false)
  private LocalDateTime updateTime;
}

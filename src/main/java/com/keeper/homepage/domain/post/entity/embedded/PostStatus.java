package com.keeper.homepage.domain.post.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStatus {

  @Column(name = "is_notice", nullable = false)
  private Boolean isNotice;

  @Column(name = "is_secret", nullable = false)
  private Boolean isSecret;

  @Column(name = "is_temp", nullable = false)
  private Boolean isTemp;

  @Builder
  private PostStatus(Boolean isNotice, Boolean isSecret, Boolean isTemp) {
    this.isNotice = isNotice;
    this.isSecret = isSecret;
    this.isTemp = isTemp;
  }

  public void update(PostStatus postStatus) {
    this.isNotice = postStatus.getIsNotice();
    this.isSecret = postStatus.getIsSecret();
    this.isTemp = postStatus.getIsTemp();
  }
}

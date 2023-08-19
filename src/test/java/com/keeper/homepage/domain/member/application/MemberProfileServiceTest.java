package com.keeper.homepage.domain.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class MemberProfileServiceTest extends IntegrationTest {

  private Member member;
  private long memberId;
  long oldThumbnailId, newThumbnailId;

  @Nested
  @DisplayName("멤버 프로필 쌈네일 수정")
  class MemberProfileThumbnailUpdate {

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.generate();
      memberId = member.getId();
    }

    @Test
    @DisplayName("멤버 프로필 썸네일 수정에 성공해야 한다.")
    void 멤버_프로필_썸네일_수정에_성공해야_한다() throws Exception {
      MockMultipartFile oldThumbnail = thumbnailTestHelper.getThumbnailFile();
      memberProfileService.updateProfileThumbnail(member, oldThumbnail);
      oldThumbnailId = member.getProfile().getThumbnail().getId();

      MockMultipartFile newThumbnail = thumbnailTestHelper.getThumbnailFile();
      memberProfileService.updateProfileThumbnail(member, newThumbnail);
      newThumbnailId = member.getProfile().getThumbnail().getId();

      em.flush();
      em.clear();

      Thumbnail findThumbnail = thumbnailRepository.findById(newThumbnailId).orElseThrow();
      Member findMember = memberRepository.findById(memberId).orElseThrow();

      assertThat(thumbnailRepository.findById(oldThumbnailId)).isEmpty();
      assertThat(thumbnailRepository.findById(newThumbnailId)).isNotEmpty();
      assertThat(findThumbnail).isEqualTo(findMember.getProfile().getThumbnail());
    }
  }
}

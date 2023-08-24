package com.keeper.homepage.domain.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import java.io.IOException;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class MemberProfileServiceTest extends IntegrationTest {

  private Member member, otherMember;
  private String randomEmail;
  private long memberId;
  long oldThumbnailId, newThumbnailId;

  @Nested
  @DisplayName("멤버 프로필 썸네일 수정")
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

  @Nested
  @DisplayName("회원 이메일 변경 테스트")
  class UpdateMemberProfileEmailAddressTest {

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();
      memberId = member.getId();
      randomEmail = memberTestHelper.generate()
          .getProfile()
          .getEmailAddress()
          .get();
    }

    @Test
    @DisplayName("이메일 중복 시 예외를 던져야 한다.")
    public void 이메일_중복_시_예외를_던져야_한다() {
      assertThrows(BusinessException.class,
          () -> memberProfileService.checkDuplicateEmailAddress(member.getProfile()
              .getEmailAddress()
              .get()));
    }

    @Test
    @DisplayName("회원 이메일 변경에 성공해야 한다.")
    public void 회원_이메일_변경에_성공해야_한다() {

      memberProfileService.updateProfileEmailAddress(member, "test@test.com", "11");

      em.flush();
      em.clear();

      Member findMember = memberFindService.findById(memberId);

      assertThat(findMember.getId()).isEqualTo(memberId);
      assertThat(findMember.getProfile().getEmailAddress().get()).isEqualTo("test@test.com");
    }
  }

}

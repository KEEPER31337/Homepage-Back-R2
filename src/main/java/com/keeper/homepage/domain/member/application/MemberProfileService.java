package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

  private final ThumbnailUtil thumbnailUtil;
  private final MemberRepository memberRepository;
  private final RedisUtil redisUtil;

  @Transactional
  public void updateProfileThumbnail(Member member, MultipartFile thumbnail) {
    thumbnailUtil.deleteFileAndEntityIfExist(
        member.getProfile().getThumbnail()
    );
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    member.getProfile().updateThumbnail(savedThumbnail);
  }

  @Transactional
  public void updateProfileEmailAddress(Member member, String email, String auth) {
    checkEmailAuth(email, auth);
    member.getProfile().updateEmailAddress(email);
  }

  public void checkDuplicateEmailAddress(String newEmail) {
    memberRepository.findByProfileEmailAddress(EmailAddress.from(newEmail))
        .ifPresent((e) -> {
          throw new BusinessException(newEmail, "newEmail", ErrorCode.MEMBER_EMAIL_DUPLICATE);
        });
  }

  private void checkEmailAuth(String email, String auth) {
    Optional<String> data = redisUtil.getData(email, String.class);
    data.map(findAuthCode -> findAuthCode.equals(auth))
        .orElseThrow(() -> new BusinessException(auth, "auth", ErrorCode.AUTH_CODE_MISMATCH));
  }
}

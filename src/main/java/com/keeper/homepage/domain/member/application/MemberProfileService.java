package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import com.keeper.homepage.global.util.mail.MailUtil;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

  private static final Random RANDOM = new Random();
  private static final String EMAIL_AUTH_CODE_KEY = "EMAIL_AUTH_";
  private static final int MAX_PASSWORD_AUTH_CODE_LENGTH = 8;
  private static final int AUTH_CODE_EXPIRE_MILLIS = 5 * 60 * 1000; // 5분


  private final ThumbnailUtil thumbnailUtil;
  private final MemberRepository memberRepository;
  private final RedisUtil redisUtil;
  private final MailUtil mailUtil;


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
    redisUtil.getData(EMAIL_AUTH_CODE_KEY + email, String.class)
            .map(value -> value.equals(auth))
            .orElseThrow(() -> new BusinessException(auth, "auth", ErrorCode.AUTH_CODE_MISMATCH));
  }

  public void sendEmailChangeAuthCode(String email) {
    String auth = generateRandomAuthCode();
    redisUtil.setDataExpire(EMAIL_AUTH_CODE_KEY + email, auth, AUTH_CODE_EXPIRE_MILLIS);
    mailUtil.sendMail(List.of(email), "KEEPER 이메일 인증 번호입니다.",
        "이메일 인증 번호: " + auth);
  }

  private static String generateRandomAuthCode() {
    char leftLimit = '0';
    char rightLimit = 'z';

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(MAX_PASSWORD_AUTH_CODE_LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}

package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_STUDENT_ID_DUPLICATE;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

  private final MemberRepository memberRepository;
  private final ThumbnailUtil thumbnailUtil;

  @Transactional
  public void updateProfileThumbnail(Member member, MultipartFile thumbnail) {
    thumbnailUtil.deleteFileAndEntityIfExist(
        member.getProfile().getThumbnail()
    );
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    member.getProfile().updateThumbnail(savedThumbnail);
  }

  private void checkIsDuplicateStudentId(StudentId studentId) {
    if (memberRepository.existsByProfileStudentId(studentId)) {
      throw new BusinessException(studentId, "studentId", MEMBER_STUDENT_ID_DUPLICATE);
    }
  }

  @Transactional
  public void updateProfile(Member member, Profile newProfile) {
    if (!member.getProfile().getStudentId().equals(newProfile.getStudentId())) {
      checkIsDuplicateStudentId(newProfile.getStudentId());
    }
    Profile profile = member.getProfile();
    profile.update(newProfile);
  }
}

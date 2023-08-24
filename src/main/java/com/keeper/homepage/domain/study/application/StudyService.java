package com.keeper.homepage.domain.study.application;

import static com.keeper.homepage.global.error.ErrorCode.STUDY_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.STUDY_LINK_NEED;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.application.convenience.StudyDeleteService;
import com.keeper.homepage.domain.study.application.convenience.StudyFindService;
import com.keeper.homepage.domain.study.dao.StudyHasMemberRepository;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.dto.response.StudyDetailResponse;
import com.keeper.homepage.domain.study.dto.response.StudyListResponse;
import com.keeper.homepage.domain.study.dto.response.StudyResponse;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

  private final StudyRepository studyRepository;
  private final StudyHasMemberRepository studyHasMemberRepository;

  private final ThumbnailUtil thumbnailUtil;
  private final StudyFindService studyFindService;
  private final StudyDeleteService studyDeleteService;
  private final MemberFindService memberFindService;

  @Transactional
  public void create(Member headMember, Study study, List<Long> memberIds, MultipartFile thumbnail) {
    checkLink(study);
    saveStudyThumbnail(study, thumbnail);

    Study savedStudy = studyRepository.save(study);

    joinAllStudyMember(headMember, memberIds, savedStudy);
  }

  private void checkLink(Study study) {
    if (study.getLink().isEmpty()) {
      throw new BusinessException(study.getId(), "studyId", STUDY_LINK_NEED);
    }
  }

  private void saveStudyThumbnail(Study study, MultipartFile thumbnail) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    study.changeThumbnail(savedThumbnail);
  }

  private void joinAllStudyMember(Member headMember, List<Long> memberIds, Study study) {
    headMember.join(study);
    memberIds.stream()
        .map(memberFindService::findById)
        .forEach(member -> member.join(study));
  }

  @Transactional
  public void delete(Member member, long studyId) {
    Study study = studyFindService.findById(studyId);

    if (!member.isHeadMember(study)) {
      throw new BusinessException(study.getId(), "study", STUDY_INACCESSIBLE);
    }
    studyDeleteService.delete(study);
  }

  public StudyDetailResponse getStudy(long studyId) {
    Study study = studyFindService.findById(studyId);
    return StudyDetailResponse.from(study);
  }

  public StudyListResponse getStudies(int year, int season) {
    List<Study> studies = studyRepository.findAllByYearAndSeason(year, season);
    List<StudyResponse> studyResponses = studies.stream()
        .map(StudyResponse::from)
        .toList();
    return StudyListResponse.from(studyResponses);
  }

  @Transactional
  public void update(Member headMember, long studyId, Study newStudy, List<Long> memberIds) {
    Study study = studyFindService.findById(studyId);
    if (!headMember.isHeadMember(study)) {
      throw new BusinessException(studyId, "studyId", STUDY_INACCESSIBLE);
    }

    checkLink(newStudy);

    study.update(newStudy);
    studyHasMemberRepository.deleteAllByStudy(study);
    joinAllStudyMember(headMember, memberIds, study);
  }

  @Transactional
  public void updateStudyThumbnail(Member member, long studyId, MultipartFile thumbnail) {
    Study study = studyFindService.findById(studyId);
    if (!member.isHeadMember(study)) {
      throw new BusinessException(studyId, "studyId", STUDY_INACCESSIBLE);
    }
    Thumbnail newThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    study.changeThumbnail(newThumbnail);
  }
}

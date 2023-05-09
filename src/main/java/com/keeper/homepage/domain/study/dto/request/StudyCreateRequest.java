package com.keeper.homepage.domain.study.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StudyCreateRequest {

  public static final int STUDY_TITLE_LENGTH = 45;
  public static final int STUDY_INFORMATION_LENGTH = 100;

  @NotBlank(message = "스터디 이름을 입력해주세요.")
  @Size(max = STUDY_TITLE_LENGTH, message = "스터디 이름은 {max}자 이하로 입력해주세요.")
  private String title;

  @NotBlank(message = "스터디 설명을 입력해주세요.")
  @Size(max = STUDY_INFORMATION_LENGTH, message = "스터디 설명은 {max}자 이하로 입력해주세요.")
  private String information;

  @NotNull(message = "스터디 년도를 입력해주세요.")
  private Integer year;

  @NotNull(message = "스터디 학기를 입력해주세요.")
  private Integer season;

  @Nullable
  private String gitLink;

  @Nullable
  private String noteLink;

  @Nullable
  private String etcLink;

  @Nullable
  private MultipartFile thumbnail;

  public Study toEntity(Member member) {
    return Study.builder()
        .title(title)
        .information(information)
        .headMember(member)
        .year(year)
        .season(season)
        .gitLink(gitLink)
        .noteLink(noteLink)
        .etcLink(etcLink)
        .build();
  }
}
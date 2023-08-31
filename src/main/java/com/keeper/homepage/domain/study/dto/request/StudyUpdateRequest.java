package com.keeper.homepage.domain.study.dto.request;

import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_INFORMATION_LENGTH;
import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_TITLE_LENGTH;
import static com.keeper.homepage.domain.study.entity.embedded.GitLink.GIT_LINK_INVALID;
import static com.keeper.homepage.domain.study.entity.embedded.GitLink.GIT_LINK_REGEX;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.embedded.GitLink;
import com.keeper.homepage.domain.study.entity.embedded.Link;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class StudyUpdateRequest {

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
  @Pattern(regexp = GIT_LINK_REGEX, message = GIT_LINK_INVALID)
  private String gitLink;

  @Nullable
  private String notionLink;

  @Nullable
  private String etcTitle;

  @Nullable
  private String etcLink;

  @NotNull(message = "회원의 ID 리스트를 입력해주세요.")
  private List<@NotNull Long> memberIds;

  public Study toEntity() {
    return Study.builder()
        .title(title)
        .information(information)
        .year(year)
        .season(season)
        .link(Link.builder()
            .gitLink(gitLink == null ? null : GitLink.from(gitLink))
            .notionLink(notionLink)
            .etcTitle(etcTitle)
            .etcLink(etcLink)
            .build())
        .build();
  }
}

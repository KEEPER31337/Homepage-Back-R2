package com.keeper.homepage.domain.post.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.file.entity.FileEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = {"post", "file"})
@IdClass(PostHasFilePK.class)
@Table(name = "posting_has_file")
public class PostHasFile {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "posting_id", nullable = false, updatable = false)
  private Post post;

  @Id
  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "file_id", nullable = false, updatable = false)
  private FileEntity file;

  @Builder
  private PostHasFile(Post post, FileEntity file) {
    this.post = post;
    this.file = file;
  }
}

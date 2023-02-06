package com.keeper.homepage.domain.file.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.keeper.homepage.domain.posting.entity.Posting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "file")
public class FileEntity {

  private static final int MAX_FILE_NAME_LENGTH = 256;
  private static final int MAX_FILE_PATH_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "file_name", nullable = false, length = MAX_FILE_NAME_LENGTH)
  private String fileName;

  @Column(name = "file_path", nullable = false, length = MAX_FILE_PATH_LENGTH)
  private String filePath;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "upload_time", nullable = false)
  private LocalDateTime uploadTime;

  @Column(name = "ip_address", nullable = false)
  private String ipAddress;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "posting_id")
  private Posting posting;

  @Builder
  private FileEntity(String fileName, String filePath, Long fileSize, LocalDateTime uploadTime,
      String ipAddress) {
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileSize = fileSize;
    this.uploadTime = uploadTime;
    this.ipAddress = ipAddress;
  }

  public void setPosting(Posting posting) {
    this.posting = posting;
  }
}

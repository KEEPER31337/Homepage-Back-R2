package com.keeper.homepage.global.util.file;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.global.util.file.exception.FileSaveFailedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public abstract class FileUtil {

  /**
   * @param file if null, return {@link Optional#empty()}
   * @return 파일 저장에 성공할 경우 {@link FileEntity} 엔티티를 담아서 반환합니다.
   * @throws FileSaveFailedException 파일 저장 실패 시 발생합니다.
   */
  public Optional<FileEntity> saveFile(MultipartFile file) {
    if (file == null) {
      return Optional.empty();
    }
    return Optional.of(save(file));
  }

  /**
   * @throws FileSaveFailedException 파일 중에 {@code null}이 있을 경우 발생합니다.
   * @see FileUtil#saveFile(MultipartFile)
   */
  public List<FileEntity> saveFiles(MultipartFile... files) {
    return Arrays.stream(files)
        .map(this::saveFile)
        .map(file -> file.orElseThrow(FileSaveFailedException::new))
        .toList();
  }

  protected abstract FileEntity save(@NonNull MultipartFile file);

  /**
   * @param fileEntity {@code @NotNull}
   * @apiNote 저장된 파일과 Entity 모두 삭제합니다.
   */
  public void deleteFileAndEntity(@NonNull FileEntity fileEntity) {
    deleteEntity(fileEntity);
    deleteFile(fileEntity);
  }

  protected abstract void deleteFile(FileEntity fileEntity);

  protected abstract void deleteEntity(FileEntity fileEntity);

}

package com.keeper.homepage.global.util.thumbnail;

import static com.keeper.homepage.global.util.thumbnail.ThumbnailType.DEFAULT;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.exception.ThumbnailException;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
public abstract class ThumbnailUtil {

  public static final String THUMBNAIL_EXTENSION = ".jpeg";

  /**
   * 타입은 기본적으로 {@link  ThumbnailType#DEFAULT} 가 들어갑니다. 그 외에는 {@code @see}를 참고해주세요.
   *
   * @see ThumbnailUtil#saveThumbnail(MultipartFile, ThumbnailType)
   */
  public Optional<Thumbnail> saveThumbnail(MultipartFile thumbnail) {
    return saveThumbnail(thumbnail, DEFAULT);
  }

  /**
   * @param thumbnail 이미지 파일이어야 합니다. if null, return {@code Optional.empty()}
   * @return 썸네일 저장에 성공할 경우 {@link Thumbnail} 엔티티를 담아서 반환합니다.
   * @throws IllegalArgumentException 이미지 파일이 아니거나 파일 이름의 길이가 너무 길 경우 발생합니다.
   * @throws NullPointerException     인자에 null이 포함되어 있을 경우 발생합니다.
   * @throws ThumbnailException       `ThumbnailUtil` 자체에 문제가 있을 경우 발생합니다.
   */
  public Optional<Thumbnail> saveThumbnail(MultipartFile thumbnail, @NonNull ThumbnailType type) {
    if (thumbnail == null) {
      return Optional.empty();
    }
    return Optional.of(this.save(thumbnail, type));
  }

  protected abstract Thumbnail save(@NonNull MultipartFile thumbnail,
      @NonNull ThumbnailType type);

  /**
   * @param thumbnail {@code @NotNull}
   * @throws org.springframework.dao.OptimisticLockingFailureException 썸네일 삭제 실패 시 발생합니다.
   * @apiNote 저장된 썸네일 파일과 Entity 모두 삭제합니다.
   */
  public void deleteFileAndEntity(@NonNull Thumbnail thumbnail) {
    deleteEntity(thumbnail);
    deleteFile(thumbnail);
  }

  protected abstract void deleteFile(Thumbnail thumbnail);

  protected abstract void deleteEntity(Thumbnail thumbnail);

  /**
   * @param thumbnail 이 {@code null}일 가능성이 있는 경우 이 메서드를 사용하시는 게 좋습니다.
   * @apiNote 썸네일이 null일 경우 아무 일도 하지 않습니다.
   * @see ThumbnailUtil#deleteFileAndEntity(Thumbnail)
   */
  public void deleteFileAndEntityIfExist(Thumbnail thumbnail) {
    if (thumbnail != null) {
      deleteFileAndEntity(thumbnail);
    }
  }

  public abstract String getThumbnailPath(String thumbnailPath);
}

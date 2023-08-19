package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

  private final ThumbnailUtil thumbnailUtil;

  public void updateProfileThumbnail(Member member, MultipartFile thumbnail) {
    thumbnailUtil.deleteFileAndEntityIfExist(
        member.getProfile().getThumbnail()
    );
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    member.updateThumbnail(savedThumbnail);
  }
}

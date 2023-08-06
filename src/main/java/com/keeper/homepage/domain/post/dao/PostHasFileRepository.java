package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.PostHasFile;
import com.keeper.homepage.domain.post.entity.PostHasFilePK;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHasFileRepository extends JpaRepository<PostHasFile, PostHasFilePK> {

  Optional<PostHasFile> findByPost(Post post);

  List<PostHasFile> findAllByPost(Post post);

  void deleteByPostAndFile(Post post, FileEntity file);
}

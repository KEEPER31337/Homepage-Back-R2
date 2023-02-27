package com.keeper.homepage.domain.file.dao;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  List<FileEntity> findAllByPost(Post post);

  List<FileEntity> findAllByPostAndIdNot(Post post, Long fileId);
}

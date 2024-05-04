package com.keeper.homepage.domain.file.dao;

import com.keeper.homepage.domain.file.entity.FileEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByFileHash(String fileHash);
}

package com.keeper.homepage.domain.file.dao;

import com.keeper.homepage.domain.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}

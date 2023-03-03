package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.BookDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDepartmentRepository extends JpaRepository<BookDepartment, Long> {

}

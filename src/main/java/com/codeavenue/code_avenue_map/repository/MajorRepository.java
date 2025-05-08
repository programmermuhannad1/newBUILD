package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    @Query("SELECT m FROM Major m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Major> searchByKeyword(@Param("keyword") String keyword);

    @Query("select count(m) >=6 from Major m")
    boolean existsAllMajors();
}

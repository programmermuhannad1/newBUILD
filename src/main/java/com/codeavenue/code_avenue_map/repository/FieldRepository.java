package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {
    @Query("SELECT f FROM Field f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Field> searchByKeyword(@Param("keyword") String keyword);
}

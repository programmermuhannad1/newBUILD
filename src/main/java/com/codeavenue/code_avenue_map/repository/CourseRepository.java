package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.field.id = :fieldId")
    void deleteByFieldId(Long fieldId);
    List<Course> findByField_Id(Long fieldId);
    List<Course> findTop3ByOrderByPriceDesc();

    List<Course> findTop3ByOrderByPriceAsc();


}


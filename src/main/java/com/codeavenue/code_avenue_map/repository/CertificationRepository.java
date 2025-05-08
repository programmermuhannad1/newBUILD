package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Certification c WHERE c.field.id = :fieldId")
    void deleteByFieldId(Long fieldId);
    List<Certification> findByField_Id(Long fieldId);
    List<Certification> findTop3ByOrderByPriceDesc();
    List<Certification> findTop3ByOrderByPriceAsc();



}

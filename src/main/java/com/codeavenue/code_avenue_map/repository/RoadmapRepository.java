package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Roadmap r WHERE r.field.id = :fieldId")
    void deleteByFieldId(Long fieldId);

    List<Roadmap> findByField_Id(Long fieldId);

}

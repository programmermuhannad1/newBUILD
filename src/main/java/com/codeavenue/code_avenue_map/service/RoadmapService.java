package com.codeavenue.code_avenue_map.service;


import com.codeavenue.code_avenue_map.exception.RoadmapNotFoundException;
import com.codeavenue.code_avenue_map.model.Field;
import com.codeavenue.code_avenue_map.model.Roadmap;
import com.codeavenue.code_avenue_map.model.dto.RoadmapCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.RoadmapDTO;
import com.codeavenue.code_avenue_map.repository.FieldRepository;
import com.codeavenue.code_avenue_map.repository.RoadmapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final FieldRepository fieldRepository;

    public RoadmapService(RoadmapRepository roadmapRepository, FieldRepository fieldRepository) {
        this.roadmapRepository = roadmapRepository;
        this.fieldRepository = fieldRepository;
    }

    @Transactional
    public RoadmapDTO createRoadmap(RoadmapCreateDTO roadmapCreateDTO) {
        Field field = fieldRepository.findById(roadmapCreateDTO.getFieldId())
                .orElseThrow(() -> new RuntimeException("⚠️ Field not found"));

        Roadmap roadmap = new Roadmap();
        roadmap.setName(roadmapCreateDTO.getName());
        roadmap.setRoadmapStages(roadmapCreateDTO.getRoadmapStages());
        roadmap.setField(field);

        Roadmap savedRoadmap = roadmapRepository.save(roadmap);
        return mapToDTO(savedRoadmap);
    }

    public List<RoadmapDTO> getAllRoadmaps() {
        return roadmapRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RoadmapDTO getRoadmapById(Long id) {
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new RoadmapNotFoundException(id));
        return mapToDTO(roadmap);
    }

    public List<RoadmapDTO> getRoadmapsByField(Long fieldId) {
        return roadmapRepository.findByField_Id(fieldId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoadmapDTO updateRoadmap(Long id, RoadmapCreateDTO updatedRoadmap, Long fieldId) {
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new RoadmapNotFoundException(id));

        if (updatedRoadmap.getName() != null && !updatedRoadmap.getName().trim().isEmpty()) {
            roadmap.setName(updatedRoadmap.getName());
        }
        if (updatedRoadmap.getRoadmapStages() != null && !updatedRoadmap.getRoadmapStages().trim().isEmpty()) {
            roadmap.setRoadmapStages(updatedRoadmap.getRoadmapStages());
        }
        if (fieldId != null) {
            Field field = fieldRepository.findById(fieldId)
                    .orElseThrow(() -> new RuntimeException("⚠️ Field not found"));
            roadmap.setField(field);
        }

        Roadmap updated = roadmapRepository.save(roadmap);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteRoadmap(Long id) {
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new RoadmapNotFoundException(id));
        roadmapRepository.delete(roadmap);
    }

    private RoadmapDTO mapToDTO(Roadmap roadmap) {
        return new RoadmapDTO(
                roadmap.getId(),
                roadmap.getName(),
                roadmap.getRoadmapStages(),
                roadmap.getField().getId()
        );
    }
}

package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.FieldNotFoundException;
import com.codeavenue.code_avenue_map.model.Field;
import com.codeavenue.code_avenue_map.model.dto.FieldCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.FieldDTO;
import com.codeavenue.code_avenue_map.repository.CertificationRepository;
import com.codeavenue.code_avenue_map.repository.CourseRepository;
import com.codeavenue.code_avenue_map.repository.FieldRepository;
import com.codeavenue.code_avenue_map.repository.RoadmapRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldService {
    private final FieldRepository fieldRepository;
    private final CourseRepository courseRepository;
    private final CertificationRepository certificationRepository;
    private final RoadmapRepository roadmapRepository;

    public FieldService(FieldRepository fieldRepository, CourseRepository courseRepository,
                        CertificationRepository certificationRepository, RoadmapRepository roadmapRepository) {
        this.fieldRepository = fieldRepository;
        this.courseRepository = courseRepository;
        this.certificationRepository = certificationRepository;
        this.roadmapRepository = roadmapRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FieldDTO createField(FieldCreateDTO fieldCreateDTO) {
        Field field = new Field();
        field.setName(fieldCreateDTO.getName());
        field.setDescription(fieldCreateDTO.getDescription());
        field.setImageUrl(fieldCreateDTO.getImageUrl());

        Field savedField = fieldRepository.save(field);
        return mapToDTO(savedField);
    }

    public FieldDTO getFieldById(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new FieldNotFoundException("Field with ID " + id + " not found"));
        return mapToDTO(field);
    }

    public List<FieldDTO> getAllFields() {
        return fieldRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FieldDTO updateField(Long id, FieldCreateDTO updatedField) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new FieldNotFoundException("Field with ID " + id + " not found"));

        field.setName(updatedField.getName());
        field.setDescription(updatedField.getDescription());
        field.setImageUrl(updatedField.getImageUrl());

        Field updated = fieldRepository.save(field);
        return mapToDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteField(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new FieldNotFoundException("Field with ID " + id + " not found"));

        courseRepository.deleteByFieldId(id);
        certificationRepository.deleteByFieldId(id);
        roadmapRepository.deleteByFieldId(id);

        fieldRepository.delete(field);
    }

    public List<String> searchFields(String keyword) {
        return fieldRepository.searchByKeyword(keyword)
                .stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    private FieldDTO mapToDTO(Field field) {
        return new FieldDTO(
                field.getId(),
                field.getName(),
                field.getDescription(),
                field.getImageUrl()
        );
    }
}

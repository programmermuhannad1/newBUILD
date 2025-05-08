package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.model.dto.CertificationCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.CertificationDTO;
import com.codeavenue.code_avenue_map.exception.CertificationNotFoundException;
import com.codeavenue.code_avenue_map.model.Certification;
import com.codeavenue.code_avenue_map.model.Field;
import com.codeavenue.code_avenue_map.repository.CertificationRepository;
import com.codeavenue.code_avenue_map.repository.FieldRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificationService {
    private final CertificationRepository certificationRepository;
    private final FieldRepository fieldRepository;

    public CertificationService(CertificationRepository certificationRepository, FieldRepository fieldRepository) {
        this.certificationRepository = certificationRepository;
        this.fieldRepository = fieldRepository;
    }

    public CertificationDTO createCertification(CertificationCreateDTO certificationDTO) {
        Field field = fieldRepository.findById(certificationDTO.getFieldId())
                .orElseThrow(() -> new RuntimeException("⚠️ Field not found with ID: " + certificationDTO.getFieldId()));

        Certification certification = new Certification();
        certification.setName(certificationDTO.getName());
        certification.setPlatform(certificationDTO.getPlatform());
        certification.setDescription(certificationDTO.getDescription());
        certification.setPrice(certificationDTO.getPrice());
        certification.setField(field);

        Certification savedCertification = certificationRepository.save(certification);
        return mapToDTO(savedCertification);
    }

    public List<CertificationDTO> getAllCertifications() {
        return certificationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CertificationDTO getCertificationById(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new CertificationNotFoundException(id));
        return mapToDTO(certification);
    }

    public List<CertificationDTO> getCertificationsByField(Long fieldId) {
        return certificationRepository.findByField_Id(fieldId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CertificationDTO updateCertification(Long id, CertificationCreateDTO updatedCertification) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new CertificationNotFoundException(id));

        Field field = fieldRepository.findById(updatedCertification.getFieldId())
                .orElseThrow(() -> new RuntimeException("⚠️ Field not found with ID: " + updatedCertification.getFieldId()));

        certification.setName(updatedCertification.getName());
        certification.setPlatform(updatedCertification.getPlatform());
        certification.setDescription(updatedCertification.getDescription());
        certification.setPrice(updatedCertification.getPrice());
        certification.setField(field);
        Certification updated = certificationRepository.save(certification);
        return mapToDTO(updated);
    }

    public void deleteCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new CertificationNotFoundException(id));
        certificationRepository.delete(certification);
    }

    private CertificationDTO mapToDTO(Certification certification) {
        return new CertificationDTO(
                certification.getId(),
                certification.getName(),
                certification.getPlatform(),
                certification.getDescription(),
                certification.getPrice(),
                certification.getField().getId(),
                certification.getField().getName()
        );
    }
}

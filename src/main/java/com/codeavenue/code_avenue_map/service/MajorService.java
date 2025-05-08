package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.MajorNotFoundException;
import com.codeavenue.code_avenue_map.model.Major;
import com.codeavenue.code_avenue_map.model.dto.MajorCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.MajorDTO;
import com.codeavenue.code_avenue_map.repository.MajorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
//hi im muhannad test
@Service
public class MajorService {
    private final MajorRepository majorRepository;


    public MajorService(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MajorDTO createMajor(MajorCreateDTO majorCreateDTO) {
        if (majorRepository.existsAllMajors()){
            throw new MajorNotFoundException("you cannot add more than six Majors");
        }

        Major major = new Major();
        major.setName(majorCreateDTO.getName());
        major.setDescription(majorCreateDTO.getDescription());
        major.setImage(majorCreateDTO.getImageUrl());

        Major savedMajor = majorRepository.save(major);
        return mapToDTO(savedMajor);
    }

    public MajorDTO getMajorById(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new MajorNotFoundException("Major with ID " + id + " not found"));
        return mapToDTO(major);
    }

    public List<MajorDTO> getAllMajors() {
        return majorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MajorDTO updateMajor(Long id, MajorCreateDTO updatedMajor) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new MajorNotFoundException("Major with ID " + id + " not found"));

        major.setName(updatedMajor.getName());
        major.setDescription(updatedMajor.getDescription());
        major.setImage(updatedMajor.getImageUrl());

        Major updated = majorRepository.save(major);
        return mapToDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new MajorNotFoundException("Major with ID " + id + " not found"));

        majorRepository.delete(major);
    }

    public List<String> searchMajors(String keyword) {
        return majorRepository.searchByKeyword(keyword)
                .stream()
                .map(Major::getName)
                .collect(Collectors.toList());
    }

    private MajorDTO mapToDTO(Major major) {
        return new MajorDTO(
                major.getId(),
                major.getName(),
                major.getDescription(),
                major.getImage()
        );
    }
}

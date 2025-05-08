package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.exception.MajorDeletionException;
import com.codeavenue.code_avenue_map.model.dto.MajorCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.MajorDTO;
import com.codeavenue.code_avenue_map.service.MajorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
public class MajorController {
    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MajorDTO> createMajor(@RequestBody MajorCreateDTO majorCreateDTO) {
        return ResponseEntity.ok(majorService.createMajor(majorCreateDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MajorDTO> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @GetMapping
    public ResponseEntity<List<MajorDTO>> getAllMajors() {
        return ResponseEntity.ok(majorService.getAllMajors());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MajorDTO> updateMajor(@PathVariable Long id, @RequestBody MajorCreateDTO updatedMajor) {
        return ResponseEntity.ok(majorService.updateMajor(id, updatedMajor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMajor(@PathVariable Long id) {
        try {
            majorService.deleteMajor(id);
            return ResponseEntity.ok("Major deleted successfully.");
        } catch (MajorDeletionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<String> searchMajors(@RequestParam String keyword) {
        return majorService.searchMajors(keyword);
    }
}

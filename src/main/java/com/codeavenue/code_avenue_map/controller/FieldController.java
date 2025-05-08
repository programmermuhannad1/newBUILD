package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.exception.FieldDeletionException;
import com.codeavenue.code_avenue_map.model.dto.FieldCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.FieldDTO;
import com.codeavenue.code_avenue_map.service.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
public class FieldController {
    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FieldDTO> createField(@RequestBody FieldCreateDTO fieldCreateDTO) {
        return ResponseEntity.ok(fieldService.createField(fieldCreateDTO));
    }


    @GetMapping("/{id}")
    public ResponseEntity<FieldDTO> getFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }


    @GetMapping
    public ResponseEntity<List<FieldDTO>> getAllFields() {
        return ResponseEntity.ok(fieldService.getAllFields());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FieldDTO> updateField(@PathVariable Long id, @RequestBody FieldCreateDTO updatedField) {
        return ResponseEntity.ok(fieldService.updateField(id, updatedField));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteField(@PathVariable Long id) {
        try {
            fieldService.deleteField(id);
            return ResponseEntity.ok("Field deleted successfully.");
        } catch (FieldDeletionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<String> searchFields(@RequestParam String keyword) {
        return fieldService.searchFields(keyword);
    }
}

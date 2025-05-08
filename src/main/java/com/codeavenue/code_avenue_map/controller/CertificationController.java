package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.model.dto.CertificationCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.CertificationDTO;
import com.codeavenue.code_avenue_map.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@Tag(name = "إدارة الشهادات", description = "Endpoints لإدارة الشهادات")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping
    @Operation(summary = "إنشاء شهادة جديدة", description = "يقوم المسؤول (ADMIN) بإنشاء شهادة جديدة في النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم إنشاء الشهادة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CertificationDTO> createCertification(@RequestBody CertificationCreateDTO certificationCreateDTO) {
        return ResponseEntity.ok(certificationService.createCertification(certificationCreateDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "الحصول على شهادة حسب المعرف", description = "إرجاع شهادة معينة بناءً على `ID` الخاص بها.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم العثور على الشهادة"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الشهادة")
    })
    public ResponseEntity<CertificationDTO> getCertificationById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @GetMapping
    @Operation(summary = "جلب جميع الشهادات", description = "إرجاع قائمة بجميع الشهادات المتاحة في النظام.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة الشهادات بنجاح")
    public ResponseEntity<List<CertificationDTO>> getAllCertifications() {
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }

    @GetMapping("/field/{fieldId}")
    @Operation(summary = "جلب الشهادات حسب المجال", description = "إرجاع قائمة بالشهادات التي تنتمي إلى مجال معين.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة الشهادات بنجاح")
    public ResponseEntity<List<CertificationDTO>> getCertificationsByField(@PathVariable Long fieldId) {
        return ResponseEntity.ok(certificationService.getCertificationsByField(fieldId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "تحديث شهادة", description = "يقوم المسؤول (ADMIN) بتحديث بيانات شهادة معينة في النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم تحديث الشهادة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الشهادة")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CertificationDTO> updateCertification(@PathVariable Long id, @RequestBody CertificationCreateDTO updatedCertificationDTO) {
        return ResponseEntity.ok(certificationService.updateCertification(id, updatedCertificationDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف شهادة", description = "يقوم المسؤول (ADMIN) بحذف شهادة معينة من النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم حذف الشهادة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الشهادة")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.ok("Certification deleted successfully.");
    }
}
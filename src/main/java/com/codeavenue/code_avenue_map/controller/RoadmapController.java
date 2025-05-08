package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.model.dto.RoadmapCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.RoadmapDTO;
import com.codeavenue.code_avenue_map.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
@CrossOrigin(origins = "*")
@Tag(name = "إدارة المسارات التعليمية", description = "Endpoints لإدارة مسارات التعلم (Roadmaps)")
public class RoadmapController {
    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping
    @Operation(summary = "إنشاء مسار تعليمي جديد", description = "يمكن للمسؤول (ADMIN) فقط إنشاء مسار تعليمي جديد.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "تم إنشاء المسار بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RoadmapDTO> createRoadmap(@RequestBody RoadmapCreateDTO roadmapCreateDTO) {
        RoadmapDTO createdRoadmap = roadmapService.createRoadmap(roadmapCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoadmap);
    }

    @GetMapping("/{id}")
    @Operation(summary = "الحصول على مسار تعليمي معين", description = "إرجاع مسار تعليمي معين بناءً على `ID` الخاص به.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم العثور على المسار"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على المسار")
    })
    public ResponseEntity<RoadmapDTO> getRoadmapById(@PathVariable Long id) {
        RoadmapDTO roadmapDTO = roadmapService.getRoadmapById(id);
        return ResponseEntity.ok(roadmapDTO);
    }

    @GetMapping
    @Operation(summary = "جلب جميع المسارات التعليمية", description = "إرجاع قائمة بجميع المسارات التعليمية المتاحة.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة المسارات التعليمية بنجاح")
    public ResponseEntity<List<RoadmapDTO>> getAllRoadmaps() {
        List<RoadmapDTO> roadmaps = roadmapService.getAllRoadmaps();
        return ResponseEntity.ok(roadmaps);
    }

    @GetMapping("/field/{fieldId}")
    @Operation(summary = "جلب المسارات التعليمية حسب المجال", description = "إرجاع قائمة بالمسارات التعليمية المرتبطة بمجال معين.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة المسارات بنجاح")
    public ResponseEntity<List<RoadmapDTO>> getRoadmapsByField(@PathVariable Long fieldId) {
        List<RoadmapDTO> roadmaps = roadmapService.getRoadmapsByField(fieldId);
        return ResponseEntity.ok(roadmaps);
    }

    @PutMapping("/{id}")
    @Operation(summary = "تحديث مسار تعليمي", description = "يمكن للمسؤول (ADMIN) فقط تحديث بيانات مسار معين.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم تحديث المسار بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على المسار")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RoadmapDTO> updateRoadmap(@PathVariable Long id, @RequestBody RoadmapCreateDTO updatedRoadmap, @RequestParam(required = false) Long fieldId) {
        RoadmapDTO roadmapDTO = roadmapService.updateRoadmap(id, updatedRoadmap, fieldId);
        return ResponseEntity.ok(roadmapDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف مسار تعليمي", description = "يمكن للمسؤول (ADMIN) فقط حذف مسار معين.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "تم حذف المسار بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على المسار")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteRoadmap(@PathVariable Long id) {
        roadmapService.deleteRoadmap(id);
        return ResponseEntity.noContent().build();
    }
}

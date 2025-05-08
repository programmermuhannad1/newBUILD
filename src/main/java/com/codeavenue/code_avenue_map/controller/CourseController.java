package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.model.dto.CourseCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.CourseDTO;
import com.codeavenue.code_avenue_map.service.CourseService;
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
@RequestMapping("/api/courses")
@Tag(name = "إدارة الدورات", description = "Endpoints لإدارة الدورات")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @PostMapping
    @Operation(summary = "إنشاء دورة جديدة", description = "يقوم المسؤول (ADMIN) بإنشاء دورة جديدة في النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم إنشاء الدورة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseCreateDTO courseCreateDTO) {
        return ResponseEntity.ok(courseService.createCourse(courseCreateDTO));
    }


    @GetMapping("/{id}")
    @Operation(summary = "الحصول على دورة حسب المعرف", description = "إرجاع دورة معينة بناءً على `ID` الخاص بها.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم العثور على الدورة"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الدورة")
    })
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }


    @GetMapping
    @Operation(summary = "جلب جميع الدورات", description = "إرجاع قائمة بجميع الدورات المتاحة في النظام.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة الدورات بنجاح")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/field/{fieldId}")
    @Operation(summary = "جلب الدورات حسب المجال", description = "إرجاع قائمة بالدورات التي تنتمي إلى مجال معين.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة الدورات بنجاح")
    public ResponseEntity<List<CourseDTO>> getCoursesByField(@PathVariable Long fieldId) {
        return ResponseEntity.ok(courseService.getCoursesByField(fieldId));
    }

    // ✅ تحديث دورة معينة (متاح فقط للإداريين)
    @PutMapping("/{id}")
    @Operation(summary = "تحديث دورة", description = "يقوم المسؤول (ADMIN) بتحديث بيانات دورة معينة في النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم تحديث الدورة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الدورة")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseCreateDTO updatedCourseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, updatedCourseDTO));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "حذف دورة", description = "يقوم المسؤول (ADMIN) بحذف دورة معينة من النظام.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم حذف الدورة بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور ADMIN"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على الدورة")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully.");
    }
}

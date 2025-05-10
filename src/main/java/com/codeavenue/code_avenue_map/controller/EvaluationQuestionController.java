package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.model.dto.EvaluationQuestionDTO;
import com.codeavenue.code_avenue_map.model.dto.EvaluationQuestionRequestDTO;
import com.codeavenue.code_avenue_map.service.EvaluationQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-questions")
public class EvaluationQuestionController {
    private final EvaluationQuestionService evaluationQuestionService;

    public EvaluationQuestionController(EvaluationQuestionService evaluationQuestionService) {
        this.evaluationQuestionService = evaluationQuestionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "إنشاء سؤال تقييم جديد (للمسؤول فقط)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "تم إنشاء السؤال بنجاح",
                    content = @Content(schema = @Schema(implementation = EvaluationQuestionDTO.class))),
            @ApiResponse(responseCode = "400", description = "بيانات السؤال غير صالحة"),
            @ApiResponse(responseCode = "403", description = "رفض الوصول"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EvaluationQuestionDTO> createQuestion(@RequestBody EvaluationQuestionRequestDTO questionRequest) {
        EvaluationQuestionDTO createdQuestion = evaluationQuestionService.createQuestion(questionRequest);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "احصل على جميع أسئلة التقييم")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "تم استرجاع الأسئلة بنجاح",
                    content = @Content(schema = @Schema(implementation = EvaluationQuestionDTO.class))),
            @ApiResponse(responseCode = "403", description = "رفض الوصول"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EvaluationQuestionDTO>> getAllQuestions() {
        List<EvaluationQuestionDTO> questions = evaluationQuestionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "احصل على سؤال التقييم عن ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "تم استرجاع السؤال بنجاح",
                    content = @Content(schema = @Schema(implementation = EvaluationQuestionDTO.class))),
            @ApiResponse(responseCode = "403", description = "رفض الوصول"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على السؤال"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EvaluationQuestionDTO> getQuestionById(@PathVariable Long id) {
        EvaluationQuestionDTO question = evaluationQuestionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "تحديث سؤال التقييم (للمسؤول فقط)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "تم تحديث السؤال بنجاح",
                    content = @Content(schema = @Schema(implementation = EvaluationQuestionDTO.class))),
            @ApiResponse(responseCode = "400", description = "بيانات السؤال غير صالحة"),
            @ApiResponse(responseCode = "403", description = "رفض الوصول"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على السؤال"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EvaluationQuestionDTO> updateQuestion(
            @PathVariable Long id,
            @RequestBody EvaluationQuestionRequestDTO updatedQuestion) {
        EvaluationQuestionDTO updated = evaluationQuestionService.updateQuestion(id, updatedQuestion);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete evaluation question (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "تم حذف السؤال بنجاح"),
            @ApiResponse(responseCode = "403", description = "رفض الوصول"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على السؤال"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        evaluationQuestionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}

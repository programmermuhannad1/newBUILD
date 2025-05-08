package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.config.JwtUtil;
import com.codeavenue.code_avenue_map.exception.ExamSubmissionException;
import com.codeavenue.code_avenue_map.exception.ExamValidationException;
import com.codeavenue.code_avenue_map.model.dto.ExamDTO;
import com.codeavenue.code_avenue_map.model.dto.ExamResultDTO;
import com.codeavenue.code_avenue_map.model.dto.ExamQuestionDTO;
import com.codeavenue.code_avenue_map.model.dto.ExamSubmissionDTO;
import com.codeavenue.code_avenue_map.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;



import java.util.Map;
//hi im muhannad

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;
    private final JwtUtil jwtUtil;

    public ExamController(ExamService examService, JwtUtil jwtUtil) {
        this.examService = examService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new exam (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam created successfully",
                    content = @Content(schema = @Schema(implementation = ExamDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid exam data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createExam(@RequestBody ExamDTO examDTO) {
        try {
            return ResponseEntity.ok(examService.createExam(examDTO));
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }


    @PostMapping("/submit")
    @Operation(summary = "Submit exam and get results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam evaluated successfully",
                    content = @Content(schema = @Schema(implementation = ExamResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid submission data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> submitExam(@RequestBody ExamSubmissionDTO submission, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ExamValidationException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7).trim();
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                throw new ExamValidationException("Invalid token payload");
            }

            submission.setUserId(userId);
            ExamResultDTO result = examService.submitExam(submission);
            return ResponseEntity.ok(result);
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (ExamSubmissionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Submission Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @PutMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update exam question (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully",
                    content = @Content(schema = @Schema(implementation = ExamQuestionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid question data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateQuestion(@RequestBody ExamQuestionDTO questionDTO) {
        try {
            if (questionDTO.getQuestionText() == null &&
                    questionDTO.getFirstMajorId() == null &&
                    questionDTO.getSecondMajorId() == null) {
                throw new ExamValidationException("At least one field must be provided for update");
            }

            if (questionDTO.getFirstMajorId() != null &&
                    questionDTO.getSecondMajorId() != null &&
                    questionDTO.getFirstMajorId().equals(questionDTO.getSecondMajorId())) {
                throw new ExamValidationException("Second major must be different from first major");
            }

            return ResponseEntity.ok(examService.updateQuestion(questionDTO));
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @DeleteMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete exam question (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteQuestion(@RequestBody ExamQuestionDTO questionDTO) {
        try {
            examService.deleteQuestion(questionDTO.getId());
            return ResponseEntity.noContent().build();
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @GetMapping("/fixed")
    @Operation(summary = "Get the fixed exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed exam retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExamDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fixed exam not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getFixedExam() {
        try {
            return ResponseEntity.ok(examService.getFixedExam());
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @GetMapping("/fixed/questions")
    @Operation(summary = "Get questions for the fixed exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExamQuestionDTO.class))),
            @ApiResponse(responseCode = "404", description = "No questions found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getFixedExamQuestions() {
        try {
            return ResponseEntity.ok(examService.getFixedExamQuestions());
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }

    @PostMapping("/fixed/questions")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add question to fixed exam (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question added successfully",
                    content = @Content(schema = @Schema(implementation = ExamQuestionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid question data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Fixed exam not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> addQuestionToFixedExam(@RequestBody ExamQuestionDTO questionDTO) {
        try {
            if (questionDTO.getFirstMajorId() == null) {
                throw new ExamValidationException("First major ID is required");
            }

            if (questionDTO.getSecondMajorId() != null &&
                    questionDTO.getSecondMajorId().equals(questionDTO.getFirstMajorId())) {
                throw new ExamValidationException("Second major must be different from first major");
            }

            return ResponseEntity.ok(examService.addQuestionToFixedExam(questionDTO));
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "An unexpected error occurred"));
        }
    }
}





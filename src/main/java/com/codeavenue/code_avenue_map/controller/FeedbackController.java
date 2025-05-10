package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.exception.FeedbackNotFoundException;
import com.codeavenue.code_avenue_map.model.dto.FeedbackDTO;
import com.codeavenue.code_avenue_map.model.dto.FeedbackRequestDTO;
import com.codeavenue.code_avenue_map.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
@Tag(name = "إدارة التقييمات", description = "Endpoints لإدارة التقييمات وإمكانية الوصول إليها")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }


    @PostMapping
    @Operation(summary = "إضافة تقييم جديد", description = "يتمكن المستخدم المسجل فقط من إضافة تقييم جديد.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم إنشاء التقييم بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، يتطلب دور USER")
    })
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        return ResponseEntity.ok(feedbackService.createFeedback(feedbackRequestDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "الحصول على تقييم معين", description = "إرجاع تقييم محدد بناءً على ID الخاص به.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم العثور على التقييم"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على التقييم")
    })
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(id)));
    }


    @GetMapping
    @Operation(summary = "جلب جميع التقييمات", description = "إرجاع قائمة بجميع التقييمات المسجلة.")
    @ApiResponse(responseCode = "200", description = "تم استرجاع قائمة التقييمات بنجاح")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }


    @GetMapping("/user")
    @Operation(summary = "جلب تقييمات المستخدم", description = "يتمكن المستخدم من استعراض تقييماته فقط.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم استرجاع التقييمات بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، لا يحق لك الوصول إلى هذه التقييمات")
    })
    @PreAuthorize("#userId == authentication.principal.user.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByUser(userId));
    }


    @PutMapping("/{id}")
    @Operation(summary = "تحديث تقييم", description = "يمكن للمستخدم تحديث تقييمه الخاص فقط.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم تحديث التقييم بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، لا يمكنك تحديث تقييم شخص آخر"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على التقييم")
    })
    @PreAuthorize("@feedbackService.isFeedbackOwner(#id, authentication.principal.user.id)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id, @RequestBody FeedbackRequestDTO updatedFeedbackDTO) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, updatedFeedbackDTO));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "حذف تقييم", description = "يمكن للمستخدم حذف تقييمه الخاص، ويمكن للمسؤول حذف أي تقييم.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "تم حذف التقييم بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، لا يمكنك حذف تقييم شخص آخر"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على التقييم")
    })
    @PreAuthorize("hasRole('ADMIN') or @feedbackService.isFeedbackOwner(#id, authentication.principal.user.id)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}

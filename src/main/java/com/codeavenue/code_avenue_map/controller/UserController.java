package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.exception.UnauthorizedAccessException;
import com.codeavenue.code_avenue_map.exception.UserNotFoundException;
import com.codeavenue.code_avenue_map.model.CustomUserDetails;
import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.UserUpdateDTO;
import com.codeavenue.code_avenue_map.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "إدارة المستخدمين", description = "Endpoints لإدارة حسابات المستخدمين العاديين")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "تحديث بيانات المستخدم", description = "يمكن للمستخدم تحديث بياناته الخاصة فقط، أو يمكن للمسؤول تحديث بيانات أي مستخدم.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "تم تحديث بيانات المستخدم بنجاح"),
            @ApiResponse(responseCode = "403", description = "ممنوع، لا يمكن تحديث بيانات مستخدم آخر"),
            @ApiResponse(responseCode = "409", description = "البريد الإلكتروني مستخدم بالفعل"),
            @ApiResponse(responseCode = "404", description = "لم يتم العثور على المستخدم")
    })
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updatedUser) {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUserObj = currentUser.getUser();

        logger.info("🔍 محاولة تعديل بيانات المستخدم: {} بواسطة المسؤول: {} | دوره: {}", id, currentUserObj.getId(), currentUserObj.getRole());

        if (!currentUserObj.getId().equals(id) && currentUserObj.getRole() != Role.ADMIN && currentUserObj.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("❌ لا يمكنك تعديل بيانات مستخدم آخر إلا إذا كنت مسؤولاً.");
        }

        User updatedUserProfile = userService.updateUserProfile(id, updatedUser, currentUserObj.getId());
        logger.info("✅ تم تحديث بيانات المستخدم ({}) بنجاح!", id);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "جلب بيانات المستخدم الحالي", description = "يتيح للمستخدم جلب بياناته الخاصة بناءً على التوكن.")
    public ResponseEntity<User> getCurrentUser() {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserById(currentUser.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException(currentUser.getUser().getId()));

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(user);
    }
}

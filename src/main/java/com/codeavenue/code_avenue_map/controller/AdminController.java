package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.exception.UnauthorizedAccessException;
import com.codeavenue.code_avenue_map.exception.UserNotFoundException;
import com.codeavenue.code_avenue_map.model.CustomUserDetails;
import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.AdminDashboardDTO;
import com.codeavenue.code_avenue_map.model.dto.UserUpdateDTO;
import com.codeavenue.code_avenue_map.service.AdminService;
import com.codeavenue.code_avenue_map.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(summary = "جلب جميع المستخدمين", description = "يتيح للمسؤول جلب جميع حسابات المستخدمين.")
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userService.getAllUsers(getCurrentAdminId());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "جلب مستخدم عبر ID", description = "يتيح للمسؤول جلب معلومات مستخدم معين.")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "تحديث دور المستخدم", description = "يتيح للمسؤول تغيير دور مستخدم آخر.")
    @Transactional
    public User updateUserRole(@PathVariable Long id, @RequestParam Role newRole) {
        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId.equals(id)) {
            throw new UnauthorizedAccessException("❌ لا يمكن للمستخدم تغيير دوره الخاص.");
        }
        return userService.updateUserRole(id, newRole, currentAdminId);
    }

    @PutMapping(value = "/users/{id}/toggle-status", consumes = "application/json")
    @Operation(summary = "تحديث حالة المستخدم", description = "يتيح للمسؤول تفعيل أو تعطيل مستخدم آخر.")
    @Transactional
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id, getCurrentAdminId());
        return ResponseEntity.ok("✅ تم تحديث حالة المستخدم بنجاح!");
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "حذف مستخدم", description = "يتيح للمسؤول حذف حساب مستخدم آخر.")
    @Transactional
    public void deleteUser(@PathVariable Long id) {
        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId.equals(id)) {
            throw new UnauthorizedAccessException("❌ لا يمكن للمسؤول حذف نفسه.");
        }
        userService.deleteUser(id, currentAdminId);
    }

    @GetMapping("/stats")
    @Operation(summary = "إحصائيات النظام", description = "يتيح للمسؤول رؤية إحصائيات النظام المتقدمة.")
    public AdminDashboardDTO getSystemStats() {
        return adminService.getStatistics();
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "تحديث بيانات مستخدم بواسطة المسؤول")
    @Transactional
    public ResponseEntity<User> adminUpdateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updatedUser) {
        User updated = userService.updateUserProfile(id, updatedUser, getCurrentAdminId());
        return ResponseEntity.ok(updated);
    }

    private Long getCurrentAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId();
        }
        throw new UnauthorizedAccessException("❌ تعذر تحديد هوية المسؤول الحالي.");
    }
}

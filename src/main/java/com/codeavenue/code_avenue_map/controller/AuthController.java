package com.codeavenue.code_avenue_map.controller;

import com.codeavenue.code_avenue_map.model.dto.LoginRequest;
import com.codeavenue.code_avenue_map.model.dto.LoginResponse;
import com.codeavenue.code_avenue_map.model.dto.RegisterRequest;
import com.codeavenue.code_avenue_map.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "التوثيق (Authentication)", description = "Endpoints لإدارة تسجيل الدخول والتسجيل والخروج")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "تسجيل الدخول", description = "يتمكن المستخدم من تسجيل الدخول واستلام التوكن.")
    @ApiResponse(responseCode = "200", description = "تم تسجيل الدخول بنجاح")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        String token = loginResponse.getToken();
        if (token != null) {
            response.setHeader("Authorization", "Bearer " + token);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/admin-login")
    @Operation(summary = "تسجيل دخول المسؤول", description = "يسمح للمسؤولين فقط بتسجيل الدخول")
    @ApiResponse(responseCode = "200", description = "تم تسجيل الدخول بنجاح كمسؤول")
    public ResponseEntity<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.adminLogin(request, response);
        String token = loginResponse.getToken();
        if (token != null) {
            response.setHeader("Authorization", "Bearer " + token);
        }
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "تسجيل مستخدم جديد", description = "يسمح بإنشاء حساب جديد")
    @ApiResponse(responseCode = "201", description = "تم إنشاء الحساب بنجاح")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "تسجيل الخروج", description = "يتم تسجيل خروج المستخدم وإبطال صلاحية التوكن.")
    @ApiResponse(responseCode = "200", description = "تم تسجيل الخروج بنجاح")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("❌ التوكن غير صحيح أو غير موجود.");
        }

        token = token.replace("Bearer ", "").trim();

        try {
            authService.logout(token);
            return ResponseEntity.ok("✅ تم تسجيل الخروج بنجاح.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ فشل في تسجيل الخروج: " + e.getMessage());
        }
    }
}

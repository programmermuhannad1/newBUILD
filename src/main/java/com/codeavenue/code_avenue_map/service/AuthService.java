package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.config.JwtUtil;
import com.codeavenue.code_avenue_map.exception.InvalidCredentialsException;
import com.codeavenue.code_avenue_map.model.CustomUserDetails;
import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.LoginRequest;
import com.codeavenue.code_avenue_map.model.dto.LoginResponse;
import com.codeavenue.code_avenue_map.model.dto.RegisterRequest;
import com.codeavenue.code_avenue_map.repository.TokenBlacklistRepository;
import com.codeavenue.code_avenue_map.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenBlacklistRepository tokenBlacklistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    public LoginResponse adminLogin(LoginRequest request, HttpServletResponse response) {
        try {
            logger.info("🔍 محاولة تسجيل دخول الإدمن للبريد الإلكتروني: {}", request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة."));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة.");
            }

            if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
                throw new InvalidCredentialsException("🚫 غير مصرح لك بالوصول إلى هذا الحساب.");
            }

            String token = jwtUtil.generateToken(new CustomUserDetails(user));

            response.setHeader("Authorization", "Bearer " + token);

            logger.info("✅ تسجيل دخول ناجح للمسؤول: {} - دور: {}", user.getEmail(), user.getRole());

            return new LoginResponse(token, user.getId().toString());
        } catch (Exception e) {
            logger.error("❌ فشل تسجيل الدخول: {}", e.getMessage());
            throw e;
        }
    }

    public LoginResponse login(LoginRequest request) {
        try {
            logger.info("🔍 محاولة تسجيل دخول للبريد الإلكتروني: {}", request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة."));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة.");
            }

            if (!user.isActive()) {
                throw new InvalidCredentialsException("🚫 الحساب غير مفعل. يرجى التواصل مع الدعم.");
            }

            String token = jwtUtil.generateToken(new CustomUserDetails(user));
            logger.info("✅ تسجيل دخول ناجح للمستخدم: {}", user.getEmail());
            return new LoginResponse(token, user.getId().toString());

        } catch (Exception e) {
            logger.error("❌ فشل تسجيل الدخول: {}", e.getMessage());
            throw e;
        }
    }

    public LoginResponse register(RegisterRequest request) {
        try {
            logger.info("🔍 محاولة تسجيل حساب جديد للبريد الإلكتروني: {}", request.getEmail());

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new InvalidCredentialsException("❌ البريد الإلكتروني مستخدم بالفعل.");
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new InvalidCredentialsException("❌ كلمة المرور غير متطابقة.");
            }

            if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("^05\\d{8}$")) {
                throw new InvalidCredentialsException("❌ رقم الهاتف يجب أن يبدأ بـ 05 ويتكون من 10 أرقام.");
            }

            User newUser = new User();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRole(Role.USER);
            newUser.setCountry(request.getCountry());
            newUser.setCity(request.getCity());
            newUser.setGender(request.getGender());
            newUser.setEducationLevel(request.getEducationLevel());
            newUser.setUniversityCollege(request.getUniversityCollege());
            newUser.setBirthDate(request.getBirthDate());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setActive(true);

            userRepository.save(newUser);

            String token = jwtUtil.generateToken(new CustomUserDetails(newUser));
            logger.info("✅ تم إنشاء الحساب بنجاح للمستخدم: {}", newUser.getEmail());
            return new LoginResponse(token, newUser.getId().toString());

        } catch (Exception e) {
            logger.error("❌ فشل تسجيل الحساب: {}", e.getMessage());
            throw e;
        }
    }


    public void logout(String token) {
        try {
            logger.info("🔍 محاولة تسجيل الخروج. التوكن: {}", token);

            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("❌ التوكن غير صالح.");
            }

            tokenBlacklistRepository.addToBlacklist(token);
            logger.info("✅ تم إضافة التوكن إلى القائمة السوداء.");

        } catch (Exception e) {
            logger.error("❌ فشل تسجيل الخروج: {}", e.getMessage());
            throw new RuntimeException("❌ تسجيل الخروج فشل، يرجى المحاولة مرة أخرى.");
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.isBlacklisted(token);
    }
}

package com.codeavenue.code_avenue_map.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("🔍 معالجة الطلب: {}", requestURI);

        if (requestURI.startsWith("/api/auth/login") ||
                requestURI.startsWith("/api/auth/register") ||
                requestURI.startsWith("/api/news") ||
                requestURI.startsWith("/api/auth/admin-login")) {
            logger.info("✅ تم تخطي المصادقة لمسار {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("🚨 لا يوجد توكن صالح في الطلب أو الرأس مفقود.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty() || !token.contains(".")) {
            logger.warn("🚨 التوكن غير صالح أو مفقود.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or empty token");
            return;
        }

        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            logger.error("🚨 فشل استخراج البريد الإلكتروني من التوكن: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails == null) {
                logger.error("🚨 المستخدم غير موجود.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            if (!jwtUtil.validateToken(token, userDetails)) {
                logger.warn("🚨 فشل التحقق من JWT للمستخدم: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}

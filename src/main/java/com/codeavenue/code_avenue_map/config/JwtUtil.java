package com.codeavenue.code_avenue_map.config;

import com.codeavenue.code_avenue_map.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET = "VGhpcy1pcy1hLXZlcnktc2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dA==";
    private final Key secretKey = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(SECRET));

    private Key getSigningKey() {
        return secretKey;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject).trim();
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class)).trim();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.trim())
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

//    public String generateToken(UserDetails userDetails) {
//        if (!(userDetails instanceof CustomUserDetails)) {
//            throw new IllegalArgumentException("UserDetails must be of type CustomUserDetails");
//        }
//
//        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
//        if (customUserDetails.getUser() == null || customUserDetails.getUser().getRole() == null) {
//            throw new IllegalArgumentException("User or role is null");
//        }
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", customUserDetails.getUser().getRole().name());
//
//        return createToken(claims, userDetails.getUsername());
//    }
public String generateToken(UserDetails userDetails) {
    if (!(userDetails instanceof CustomUserDetails)) {
        throw new IllegalArgumentException("UserDetails must be of type CustomUserDetails");
    }

    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
    if (customUserDetails.getUser() == null || customUserDetails.getUser().getRole() == null) {
        throw new IllegalArgumentException("User or role is null");
    }

    Map<String, Object> claims = new HashMap<>();
    claims.put("role", customUserDetails.getUser().getRole().name());
    claims.put("userId", customUserDetails.getUser().getId()); // F

    return createToken(claims, userDetails.getUsername());
}

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token) {
        try {
            if (isTokenExpired(token)) {
                return false;
            }
            extractEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class)); //F
    }
}

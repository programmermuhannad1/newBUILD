package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.UnauthorizedAccessException;
import com.codeavenue.code_avenue_map.model.CustomUserDetails;
import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.AdminDashboardDTO;
import com.codeavenue.code_avenue_map.model.dto.CertificationDTO;
import com.codeavenue.code_avenue_map.model.dto.CourseDTO;
import com.codeavenue.code_avenue_map.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// بهذا


@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final CourseRepository courseRepository;
    private final CertificationRepository certificationRepository;
    private final RoadmapRepository roadmapRepository;

    public AdminService(UserRepository userRepository,
                        FieldRepository fieldRepository,
                        CourseRepository courseRepository,
                        CertificationRepository certificationRepository,
                        RoadmapRepository roadmapRepository) {
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.courseRepository = courseRepository;
        this.certificationRepository = certificationRepository;
        this.roadmapRepository = roadmapRepository;
    }

    public AdminDashboardDTO getStatistics() {
        CustomUserDetails currentUser =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getUser().getRole() != Role.ADMIN &&
                currentUser.getUser().getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("ليس لديك صلاحية الوصول إلى هذه البيانات.");
        }

        logger.info("🔍 استرجاع إحصائيات النظام بواسطة المسؤول: {}", currentUser.getUsername());

        long totalUsers = userRepository.count();

        Map<Role, Long> usersByRole = Arrays.stream(Role.values())
                .collect(Collectors.toMap(
                        role -> role,
                        userRepository::countByRole
                ));

        Map<String, Long> usersByGender = new HashMap<>();
        usersByGender.put("MALE",   userRepository.countByGender(User.Gender.MALE));
        usersByGender.put("FEMALE", userRepository.countByGender(User.Gender.FEMALE));

        List<Integer> ages = userRepository.findAll().stream()
                .filter(u -> u.getBirthDate() != null)
                .map(u -> Period.between(u.getBirthDate(), LocalDate.now()).getYears())
                .collect(Collectors.toList());
        long minAge = ages.stream().min(Integer::compare).orElse(0);
        long maxAge = ages.stream().max(Integer::compare).orElse(0);
        Map<String, Long> ageStats = Map.of("minAge", minAge, "maxAge", maxAge);

        List<CourseDTO> top3ExpensiveCourses = courseRepository.findTop3ByOrderByPriceDesc().stream()
                .map(c -> new CourseDTO(
                        c.getId(),
                        c.getName(),
                        c.getPlatform(),
                        c.getDescription(),
                        c.getPrice(),
                        c.getField().getId()
                ))
                .collect(Collectors.toList());
        List<CourseDTO> top3CheapestCourses = courseRepository.findTop3ByOrderByPriceAsc().stream()
                .map(c -> new CourseDTO(
                        c.getId(),
                        c.getName(),
                        c.getPlatform(),
                        c.getDescription(),
                        c.getPrice(),
                        c.getField().getId()
                ))
                .collect(Collectors.toList());

        List<CertificationDTO> top3ExpensiveCertifications = certificationRepository.findTop3ByOrderByPriceDesc().stream()
                .map(c -> new CertificationDTO(
                        c.getId(),
                        c.getName(),
                        c.getPlatform(),
                        c.getDescription(),
                        c.getPrice(),
                        c.getField().getId(),
                        c.getField().getName()
                ))
                .collect(Collectors.toList());
        List<CertificationDTO> top3CheapestCertifications = certificationRepository.findTop3ByOrderByPriceAsc().stream()
                .map(c -> new CertificationDTO(
                        c.getId(),
                        c.getName(),
                        c.getPlatform(),
                        c.getDescription(),
                        c.getPrice(),
                        c.getField().getId(),
                        c.getField().getName()
                ))
                .collect(Collectors.toList());

        return new AdminDashboardDTO(
                totalUsers,
                usersByRole,
                fieldRepository.count(),
                courseRepository.count(),
                certificationRepository.count(),
                roadmapRepository.count(),
                0L,
                usersByGender,
                ageStats,
                top3ExpensiveCourses,
                top3CheapestCourses,
                top3ExpensiveCertifications,
                top3CheapestCertifications
        );
    }
}

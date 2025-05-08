package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalUsers;
    private Map<Role, Long> usersByRole;
    private Long totalFields;
    private Long totalCourses;
    private Long totalCertifications;
    private Long totalEvaluations;
    private Long newUsersInPeriod;

    private Map<String, Long> usersByGender;
    private Map<String, Long> ageStats;

    private List<CourseDTO> top3ExpensiveCourses;
    private List<CourseDTO> top3CheapestCourses;
    private List<CertificationDTO> top3ExpensiveCertifications;
    private List<CertificationDTO> top3CheapestCertifications;
}

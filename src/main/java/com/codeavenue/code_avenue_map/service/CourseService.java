package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.CourseDeletionException;
import com.codeavenue.code_avenue_map.exception.CourseNotFoundException;
import com.codeavenue.code_avenue_map.model.Course;
import com.codeavenue.code_avenue_map.model.Field;
import com.codeavenue.code_avenue_map.model.dto.CourseCreateDTO;
import com.codeavenue.code_avenue_map.model.dto.CourseDTO;
import com.codeavenue.code_avenue_map.repository.CourseRepository;
import com.codeavenue.code_avenue_map.repository.FieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final FieldRepository fieldRepository;

    public CourseService(CourseRepository courseRepository, FieldRepository fieldRepository) {
        this.courseRepository = courseRepository;
        this.fieldRepository = fieldRepository;
    }

    @Transactional
    public CourseDTO createCourse(CourseCreateDTO courseDTO) {
        Field field = fieldRepository.findById(courseDTO.getFieldId())
                .orElseThrow(() -> new RuntimeException("⚠️ Field not found with ID: " + courseDTO.getFieldId()));

        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setPlatform(courseDTO.getPlatform());
        course.setDescription(courseDTO.getDescription());
        course.setPrice(courseDTO.getPrice());
        course.setField(field);

        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("⚠️ Course not found with ID: " + id));
        return mapToDTO(course);
    }

    public List<CourseDTO> getCoursesByField(Long fieldId) {
        return courseRepository.findByField_Id(fieldId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public CourseDTO updateCourse(Long id, CourseCreateDTO updatedCourseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("⚠️ Course not found with ID: " + id));

        Field field = fieldRepository.findById(updatedCourseDTO.getFieldId())
                .orElseThrow(() -> new RuntimeException("⚠️ Field not found with ID: " + updatedCourseDTO.getFieldId()));

        if (updatedCourseDTO.getName() != null && !updatedCourseDTO.getName().trim().isEmpty()) {
            course.setName(updatedCourseDTO.getName());
        }
        if (updatedCourseDTO.getPlatform() != null && !updatedCourseDTO.getPlatform().trim().isEmpty()) {
            course.setPlatform(updatedCourseDTO.getPlatform());
        }
        if (updatedCourseDTO.getDescription() != null && !updatedCourseDTO.getDescription().trim().isEmpty()) {
            course.setDescription(updatedCourseDTO.getDescription());
        }
        if (updatedCourseDTO.getPrice() > 0) {
            course.setPrice(updatedCourseDTO.getPrice());
        }
        course.setField(field);

        Course updated = courseRepository.save(course);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("⚠️ Course not found with ID: " + id));

        try {
            courseRepository.delete(course);
        } catch (Exception e) {
            throw new CourseDeletionException("⚠️ Failed to delete course with ID " + id + ". It may be associated with other data.");
        }
    }

    private CourseDTO mapToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getPlatform(),
                course.getDescription(),
                course.getPrice(),
                (course.getField() != null) ? course.getField().getId() : null
        );
    }
}

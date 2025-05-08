package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.EmailAlreadyExistsException;
import com.codeavenue.code_avenue_map.exception.UnauthorizedAccessException;
import com.codeavenue.code_avenue_map.exception.UserNotFoundException;
import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.UserUpdateDTO;
import com.codeavenue.code_avenue_map.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getAllUsers(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("لا تملك صلاحية الوصول إلى جميع المستخدمين.");
        }

        return userRepository.findAll();
    }

    @Transactional
    public void toggleUserStatus(Long userId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("المسؤول غير موجود"));

        if (admin.getRole() != Role.ADMIN && admin.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("❌ ليس لديك الصلاحيات لتغيير حالة المستخدم.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("المستخدم غير موجود"));

        User.Status newStatus = (user.getStatus() == User.Status.ACTIVE) ? User.Status.INACTIVE : User.Status.ACTIVE;

        user.setStatus(newStatus);
        userRepository.save(user);

        logger.info("✅ تم تغيير حالة المستخدم (ID: {}) إلى: {}", userId, newStatus);
    }

    public User updateUserRole(Long id, Role newRole, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("لا يمكنك تعديل دور المستخدم.");
        }

        return userRepository.findById(id).map(user -> {
            user.setRole(newRole);
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User updateUserProfile(Long id, UserUpdateDTO updatedUserDTO, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        if (!id.equals(currentUserId) && currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("❌ لا يمكنك تعديل حساب شخص آخر إلا إذا كنت مسؤولاً.");
        }

        if (updatedUserDTO.getEmail() != null && !updatedUserDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updatedUserDTO.getEmail())) {
                throw new EmailAlreadyExistsException("❌ البريد الإلكتروني مستخدم بالفعل.");
            }
            user.setEmail(updatedUserDTO.getEmail());
        }

        if (updatedUserDTO.getFirstName() != null) {
            user.setFirstName(updatedUserDTO.getFirstName());
        }
        if (updatedUserDTO.getLastName() != null) {
            user.setLastName(updatedUserDTO.getLastName());
        }
        if (updatedUserDTO.getCountry() != null) {
            user.setCountry(updatedUserDTO.getCountry());
        }
        if (updatedUserDTO.getCity() != null) {
            user.setCity(updatedUserDTO.getCity());
        }

        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteUser(Long id, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        if (!id.equals(currentUserId) && currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException(" لا يمكنك حذف حساب شخص آخر.");
        }

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new UnauthorizedAccessException("❌ لا يمكنك حذف حساب المشرف العام (SUPER_ADMIN).");
        }

        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);

        logger.info("✅ تم تعطيل حساب المستخدم (ID: {}) بدلاً من حذفه.", id);
    }
}

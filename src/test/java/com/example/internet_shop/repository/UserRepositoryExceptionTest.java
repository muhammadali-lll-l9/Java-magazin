package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryExceptionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
    }

    @Test
    void saveUser_WithDuplicateUsername_ShouldThrowException() {
        User user1 = new User();
        user1.setUsername("duplicate_user");
        user1.setPassword("pass1");
        user1.setEmail("email1@test.com");
        user1.setRole(userRole);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("duplicate_user");
        user2.setPassword("pass2");
        user2.setEmail("email2@test.com");
        user2.setRole(userRole);

        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}


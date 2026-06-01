package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;

    @BeforeEach
    void setUp() {
        Optional<Role> existingRole = roleRepository.findByName("USER");
        if (existingRole.isPresent()) {
            userRole = existingRole.get();
        } else {
            userRole = new Role("USER");
            userRole = roleRepository.save(userRole);
        }
    }

    @Test
    void saveAndFindUser_ShouldWork() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded123");
        user.setEmail("test@example.com");
        user.setRole(userRole);

        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();

        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByUsername_WhenNotExists_ReturnsEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent12345");
        assertThat(found).isEmpty();
    }
}

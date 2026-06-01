package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_ReturnsRole() {
        Role role = new Role("TEST_ROLE");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("TEST_ROLE");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("TEST_ROLE");
    }
}


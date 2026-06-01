package com.example.internet_shop.config;

import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_WhenExists_ReturnsUserDetails() {
        Role role = new Role("USER");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded");
        user.setRole(role);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        var details = userDetailsService.loadUserByUsername("testuser");

        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getAuthorities()).hasSize(1);
    }

    @Test
    void loadUserByUsername_WhenNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}


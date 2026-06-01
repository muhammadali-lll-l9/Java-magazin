package com.example.internet_shop.controller;

import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.repository.RoleRepository;
import com.example.internet_shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");
    }

    @Test
    void testShowRegisterForm() {
        String result = registerController.showRegisterForm();
        assertEquals("auth/register", result);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String result = registerController.registerUser("newuser", "password123", "newuser@mail.ru");

        assertEquals("redirect:/login?registered=true", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void testRegisterUser_UsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        String result = registerController.registerUser("existinguser", "password123", "existing@mail.ru");

        assertEquals("redirect:/register?error=true", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            registerController.registerUser("newuser", "password123", "newuser@mail.ru");
        });
    }
}
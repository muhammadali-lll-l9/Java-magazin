package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Cart;
import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Optional<Role> existingRole = roleRepository.findByName("USER");
        Role userRole;
        if (existingRole.isPresent()) {
            userRole = existingRole.get();
        } else {
            userRole = new Role("USER");
            userRole = roleRepository.save(userRole);
        }

        testUser = new User();
        testUser.setUsername("cartuser");
        testUser.setPassword("pass");
        testUser.setEmail("cart@example.com");
        testUser.setRole(userRole);
        testUser = userRepository.save(testUser);
    }

    @Test
    void saveAndFindCart_ShouldWork() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setTotalPrice(BigDecimal.valueOf(100.00));

        Cart saved = cartRepository.save(cart);
        assertThat(saved.getId()).isNotNull();

        Optional<Cart> found = cartRepository.findByUser(testUser);
        assertThat(found).isPresent();
        assertThat(found.get().getTotalPrice()).isEqualByComparingTo("100.00");
    }
}



package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Order;
import com.example.internet_shop.entity.Role;
import com.example.internet_shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

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
        testUser.setUsername("orderuser");
        testUser.setPassword("pass");
        testUser.setEmail("order@example.com");
        testUser.setRole(userRole);
        testUser = userRepository.save(testUser);
    }

    @Test
    void findByUser_ReturnsOrders() {
        Order order = new Order();
        order.setUser(testUser);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus("NEW");
        order.setTotalAmount(BigDecimal.valueOf(200));
        order.setTotalPrice(BigDecimal.valueOf(200));  // ← ДОБАВИЛ ЭТУ СТРОКУ!
        order.setCustomerName("Test");
        orderRepository.save(order);

        List<Order> orders = orderRepository.findByUser(testUser);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo("NEW");
    }
}


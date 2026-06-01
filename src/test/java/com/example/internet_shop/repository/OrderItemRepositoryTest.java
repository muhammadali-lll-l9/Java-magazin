package com.example.internet_shop.repository;

import com.example.internet_shop.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // ← ДОБАВЬ ЭТУ СТРОКУ!
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Order testOrder;

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

        String uniqueUsername = "orderitemuser_" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(uniqueUsername);
        user.setPassword("pass");
        user.setEmail("orderitem@example.com");
        user.setRole(userRole);
        user = userRepository.save(user);

        testOrder = new Order();
        testOrder.setUser(user);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
        testOrder.setStatus("NEW");
        testOrder.setTotalAmount(BigDecimal.valueOf(100));
        testOrder.setTotalPrice(BigDecimal.valueOf(100));
        testOrder = orderRepository.save(testOrder);

        Category category = new Category("Cat", "Desc");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Product");
        product.setPrice(BigDecimal.valueOf(50));
        product.setStock(5);
        product.setCategory(category);
        product = productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(50));
        orderItem.setPriceAtPurchase(BigDecimal.valueOf(50));
        orderItemRepository.save(orderItem);
    }

    @Test
    void findByOrder_ReturnsItems() {
        List<OrderItem> items = orderItemRepository.findByOrder(testOrder);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void deleteByOrderId_RemovesItems() {
        orderItemRepository.deleteByOrderId(testOrder.getId());
        List<OrderItem> items = orderItemRepository.findByOrder(testOrder);
        assertThat(items).isEmpty();
    }
}


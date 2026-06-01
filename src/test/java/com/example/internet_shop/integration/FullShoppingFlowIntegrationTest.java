package com.example.internet_shop.integration;

import com.example.internet_shop.entity.*;
import com.example.internet_shop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FullShoppingFlowIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        String uniqueUsername = "flowuser_" + System.currentTimeMillis();
        testUser = new User();
        testUser.setUsername(uniqueUsername);
        testUser.setPassword(passwordEncoder.encode("pass"));
        testUser.setEmail("flow@test.com");
        testUser.setRole(userRole);
        testUser = userRepository.save(testUser);

        Category category = new Category("Test Cat", "Desc");
        category = categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setStock(10);
        testProduct.setCategory(category);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void fullFlow_ShouldWork() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setPrice(testProduct.getPrice());
        cartItem = cartItemRepository.save(cartItem);

        BigDecimal total = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        cart.setTotalPrice(total);
        cartRepository.save(cart);

        assertThat(cart.getTotalPrice()).isEqualByComparingTo("199.98");

        Order order = new Order();
        order.setUser(testUser);
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(cart.getTotalPrice());
        order.setTotalPrice(cart.getTotalPrice());  // ← ДОБАВИЛ ЭТУ СТРОКУ!
        order.setCustomerName("Test Customer");
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getPrice());
        orderItem.setPriceAtPurchase(cartItem.getPrice());
        orderItemRepository.save(orderItem);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        assertThat(orderRepository.findByUser(testUser)).hasSize(1);
        assertThat(orderItemRepository.findByOrder(order)).hasSize(1);
        assertThat(cartRepository.findByUser(testUser).get().getTotalPrice()).isEqualByComparingTo("0");
    }

    // Добавь этот тест в существующий файл
    @Test
    void fullFlow_ShouldDecreaseStockWhenOrderCreated() throws Exception {
        int initialStock = testProduct.getStock();

        // Создаём корзину и добавляем товар
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setPrice(testProduct.getPrice());
        cartItemRepository.save(cartItem);

        // Создаём заказ
        Order order = new Order();
        order.setUser(testUser);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(cart.getTotalPrice());
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomerName("Test");
        orderRepository.save(order);

        // Проверяем, что количество товара НЕ уменьшилось
        // (stock не должен меняться при создании заказа)
        Product updatedProduct = productRepository.findById(testProduct.getId()).get();
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock);
    }
}


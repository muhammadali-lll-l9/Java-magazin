package com.example.internet_shop.repository;

import com.example.internet_shop.entity.*;
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
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Cart testCart;
    private Product testProduct;

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

        // ✅ ИСПРАВЛЕНО: уникальное имя через timestamp
        String uniqueUsername = "cartuser_" + System.currentTimeMillis();

        User user = new User();
        user.setUsername(uniqueUsername);  // ← теперь уникальное!
        user.setPassword("pass");
        user.setEmail("user_" + uniqueUsername + "@example.com");
        user.setRole(userRole);
        user = userRepository.save(user);

        testCart = new Cart();
        testCart.setUser(user);
        testCart.setTotalPrice(BigDecimal.ZERO);
        testCart = cartRepository.save(testCart);

        Category category = new Category("Test", "Desc");
        category = categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setName("Product");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setStock(10);
        testProduct.setCategory(category);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void findByCartAndProduct_WhenExists_ReturnsItem() {
        CartItem item = new CartItem();
        item.setCart(testCart);
        item.setProduct(testProduct);
        item.setQuantity(2);
        item.setPrice(testProduct.getPrice());
        cartItemRepository.save(item);

        Optional<CartItem> found = cartItemRepository.findByCartAndProduct(testCart, testProduct);

        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(2);
    }
}


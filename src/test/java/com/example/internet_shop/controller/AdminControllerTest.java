package com.example.internet_shop.controller;

import com.example.internet_shop.entity.*;
import com.example.internet_shop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private Product product;
    private Category category;
    private User user;
    private Order order;
    private Cart cart;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus("PENDING");

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);
    }

    @Test
    void testDashboard() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(productRepository.count()).thenReturn(1L);
        when(categoryRepository.count()).thenReturn(1L);
        when(userRepository.count()).thenReturn(1L);
        when(orderRepository.count()).thenReturn(1L);

        String result = adminController.dashboard(model, "products");

        assertEquals("admin/dashboard", result);
        verify(model, times(1)).addAttribute(eq("products"), anyList());
        verify(model, times(1)).addAttribute(eq("activeSection"), eq("products"));
    }

    @Test
    void testViewOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(Arrays.asList());

        String result = adminController.viewOrder(1L, model);

        assertEquals("admin/order-details", result);
        verify(model, times(1)).addAttribute(eq("order"), eq(order));
        verify(model, times(1)).addAttribute(eq("orderItems"), anyList());
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        String result = adminController.updateOrderStatus(1L, "COMPLETED", "orders");

        assertEquals("redirect:/admin/dashboard?section=orders", result);
        verify(orderRepository, times(1)).save(order);
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    void testSaveProduct() {
        product.setCategory(category);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = adminController.saveProduct(product, "products");

        assertEquals("redirect:/admin/dashboard?section=products", result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testSaveProductWithoutCategory() {
        product.setCategory(null);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = adminController.saveProduct(product, "products");

        assertEquals("redirect:/admin/dashboard?section=products", result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testEditProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(productRepository.count()).thenReturn(1L);
        when(categoryRepository.count()).thenReturn(1L);
        when(userRepository.count()).thenReturn(1L);
        when(orderRepository.count()).thenReturn(1L);

        String result = adminController.editProduct(1L, model, "products");

        assertEquals("admin/dashboard", result);
        verify(model, times(1)).addAttribute(eq("product"), eq(product));
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(orderItemRepository).deleteByProductId(1L);
        doNothing().when(productRepository).deleteById(1L);

        String result = adminController.deleteProduct(1L, "products");

        assertEquals("redirect:/admin/dashboard?section=products", result);
        verify(orderItemRepository, times(1)).deleteByProductId(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSaveCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        String result = adminController.saveCategory(null, "New Category", "categories");

        assertEquals("redirect:/admin/dashboard?section=categories", result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        String result = adminController.saveCategory(1L, "Updated Category", "categories");

        assertEquals("redirect:/admin/dashboard?section=categories", result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        String result = adminController.deleteCategory(1L, "categories");

        assertEquals("redirect:/admin/dashboard?section=categories", result);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));
        doNothing().when(cartItemRepository).deleteByCartId(1L);
        doNothing().when(cartRepository).delete(cart);
        doNothing().when(orderItemRepository).deleteByOrderId(1L);
        doNothing().when(orderRepository).delete(order);
        doNothing().when(userRepository).delete(user);

        String result = adminController.deleteUser(1L, "users");

        assertEquals("redirect:/admin/dashboard?section=users", result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserWithoutCart() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));
        doNothing().when(orderItemRepository).deleteByOrderId(1L);
        doNothing().when(orderRepository).delete(order);
        doNothing().when(userRepository).delete(user);

        String result = adminController.deleteUser(1L, "users");

        assertEquals("redirect:/admin/dashboard?section=users", result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderItemRepository).deleteByOrderId(1L);
        doNothing().when(orderRepository).deleteById(1L);

        String result = adminController.deleteOrder(1L, "orders");

        assertEquals("redirect:/admin/dashboard?section=orders", result);
        verify(orderItemRepository, times(1)).deleteByOrderId(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }
}
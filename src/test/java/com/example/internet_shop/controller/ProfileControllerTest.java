package com.example.internet_shop.controller;

import com.example.internet_shop.entity.*;
import com.example.internet_shop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

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
    private UserDetails userDetails;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProfileController profileController;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Order order;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.valueOf(100));
        cart.setCartItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.valueOf(100));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.valueOf(100));

        cart.getCartItems().add(cartItem);
    }

    @Test
    void testProfile() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String result = profileController.profile(userDetails, model);

        assertEquals("profile", result);
        verify(model, times(1)).addAttribute(eq("user"), eq(user));
    }

    @Test
    void testProfile_UserNotLoggedIn() {
        String result = profileController.profile(null, model);

        assertEquals("redirect:/login", result);
    }

    @Test
    void testProfile_UserNotFound() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        String result = profileController.profile(userDetails, model);

        assertEquals("redirect:/login", result);
    }

    @Test
    void testMyOrders() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        String result = profileController.myOrders(userDetails, model);

        assertEquals("orders", result);
        verify(model, times(1)).addAttribute(eq("orders"), anyList());
        verify(model, times(1)).addAttribute(eq("user"), eq(user));
    }

    @Test
    void testCheckout() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(Arrays.asList(cartItem));

        String result = profileController.checkout(userDetails, model);

        assertEquals("checkout", result);
        verify(model, times(1)).addAttribute(eq("cart"), eq(cart));
        verify(model, times(1)).addAttribute(eq("cartItems"), anyList());
        verify(model, times(1)).addAttribute(eq("user"), eq(user));
    }

    @Test
    void testCheckout_EmptyCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(new ArrayList<>());

        String result = profileController.checkout(userDetails, model);

        assertEquals("redirect:/cart", result);
    }

    @Test
    void testCheckout_NoCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        String result = profileController.checkout(userDetails, model);

        assertEquals("redirect:/cart", result);
    }

    @Test
    void testProcessPaymentAndOrder() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(Arrays.asList(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());
        doNothing().when(cartItemRepository).deleteAll(anyList());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = profileController.processPaymentAndOrder(
                userDetails, "Test Customer", "1234567890",
                "Test Address", "12345", "Test City", "4111111111111111",
                redirectAttributes);

        assertEquals("redirect:/profile/orders", result);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(cartItemRepository, times(1)).deleteAll(anyList());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("orderSuccess"), eq(true));
    }

    @Test
    void testProcessPaymentAndOrder_EmptyCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(new ArrayList<>());

        String result = profileController.processPaymentAndOrder(
                userDetails, "Test Customer", "1234567890",
                "Test Address", "12345", "Test City", "4111111111111111",
                redirectAttributes);

        assertEquals("redirect:/cart", result);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
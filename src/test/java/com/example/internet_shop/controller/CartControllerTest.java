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
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetails userDetails;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @InjectMocks
    private CartController cartController;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

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
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCartItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void testAddToCart_UserNotLoggedIn() {
        // Убираем лишний стаб - просто проверяем что userDetails == null
        String result = cartController.addToCart(1L, null, session);

        assertEquals("redirect:/login?cart=true", result);
        verify(session, times(1)).setAttribute(eq("pendingProductId"), eq(1L));
        // Не вызываем verify для userDetails, так как он null
    }

    @Test
    void testAddToCart_UserLoggedIn() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = cartController.addToCart(1L, userDetails, session);

        assertEquals("redirect:/cart", result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_ProductAlreadyInCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = cartController.addToCart(1L, userDetails, session);

        assertEquals("redirect:/cart", result);
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void testAddToCart_CreateNewCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product))).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        String result = cartController.addToCart(1L, userDetails, session);

        assertEquals("redirect:/cart", result);
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        String result = cartController.addToCart(1L, userDetails, session);

        assertEquals("redirect:/products", result);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testViewCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        cart.getCartItems().add(cartItem);

        String result = cartController.viewCart(userDetails, model);

        assertEquals("cart", result);
        verify(model, times(1)).addAttribute(eq("user"), eq(user));
        verify(model, times(1)).addAttribute(eq("cart"), eq(cart));
    }

    @Test
    void testViewCart_UserNotLoggedIn() {
        String result = cartController.viewCart(null, model);

        assertEquals("redirect:/login", result);
    }

    @Test
    void testViewCart_NoCart() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        String result = cartController.viewCart(userDetails, model);

        assertEquals("cart", result);
        verify(model, times(1)).addAttribute(eq("cartItems"), anyList());
    }

    @Test
    void testUpdateQuantity_IncreaseQuantity() {
        cartItem.setQuantity(1);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = cartController.updateQuantity(1L, 3);

        assertEquals("redirect:/cart", result);
        assertEquals(3, cartItem.getQuantity());
    }

    @Test
    void testUpdateQuantity_RemoveItem() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).deleteById(1L);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String result = cartController.updateQuantity(1L, 0);

        assertEquals("redirect:/cart", result);
        verify(cartItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateQuantity_ItemNotFound() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        String result = cartController.updateQuantity(1L, 2);

        assertEquals("redirect:/cart", result);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void testRemoveFromCart() {
        doNothing().when(cartItemRepository).deleteById(1L);

        String result = cartController.removeFromCart(1L);

        assertEquals("redirect:/cart", result);
        verify(cartItemRepository, times(1)).deleteById(1L);
    }
}
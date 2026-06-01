package com.example.internet_shop.controller;

import com.example.internet_shop.entity.Cart;
import com.example.internet_shop.entity.CartItem;
import com.example.internet_shop.entity.Product;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.repository.CartItemRepository;
import com.example.internet_shop.repository.CartRepository;
import com.example.internet_shop.repository.ProductRepository;
import com.example.internet_shop.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository,
                          ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpSession session) {
        if (userDetails == null) {
            session.setAttribute("pendingProductId", productId);
            return "redirect:/login?cart=true";
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }

        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalPrice(BigDecimal.ZERO);
            cart = cartRepository.save(cart);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            newItem.setPrice(product.getPrice());
            cartItemRepository.save(newItem);
        }

        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cartRepository.save(cart);

        return "redirect:/cart";
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getCartItems() != null ? cart.getCartItems() : java.util.Collections.emptyList());

        return "cart";
    }

    @GetMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        CartItem cartItem = cartItemRepository.findById(id).orElse(null);
        if (cartItem != null && quantity > 0) {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);

            Cart cart = cartItem.getCart();
            BigDecimal total = cart.getCartItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotalPrice(total);
            cartRepository.save(cart);
        } else if (cartItem != null && quantity <= 0) {
            cartItemRepository.deleteById(id);
            Cart cart = cartItem.getCart();
            BigDecimal total = cart.getCartItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotalPrice(total);
            cartRepository.save(cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
        return "redirect:/cart";
    }
}


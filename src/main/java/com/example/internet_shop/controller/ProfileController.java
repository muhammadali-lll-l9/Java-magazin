package com.example.internet_shop.controller;

import com.example.internet_shop.entity.Cart;
import com.example.internet_shop.entity.CartItem;
import com.example.internet_shop.entity.Order;
import com.example.internet_shop.entity.OrderItem;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.repository.CartItemRepository;
import com.example.internet_shop.repository.CartRepository;
import com.example.internet_shop.repository.OrderItemRepository;
import com.example.internet_shop.repository.OrderRepository;
import com.example.internet_shop.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public ProfileController(UserRepository userRepository,
                             OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             CartRepository cartRepository,
                             CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        return "orders";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = cartRepository.findByUser(user).orElse(null);
        List<CartItem> items = loadCartItems(cart);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", items);
        model.addAttribute("user", user);
        return "checkout";
    }

    @Transactional
    @PostMapping("/process-payment-and-order")
    public String processPaymentAndOrder(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam String customerName,
                                         @RequestParam String phone,
                                         @RequestParam String address,
                                         @RequestParam String postalCode,
                                         @RequestParam String city,
                                         @RequestParam String cardNumber,
                                         RedirectAttributes redirectAttributes) {
        User user = requireUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartRepository.findByUser(user).orElse(null);
        List<CartItem> items = loadCartItems(cart);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(now);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setStatus("ЗАКАЗ ПРИНЯТ");
        order.setTotalAmount(total);
        order.setTotalPrice(total);
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress("г. " + city + ", индекс " + postalCode + ", " + address);
        order = orderRepository.save(order);

        for (CartItem item : items) {
            if (item.getProduct() == null) continue;
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setPriceAtPurchase(item.getPrice());
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(items);
        if (cart != null) {
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
        }

        redirectAttributes.addFlashAttribute("orderSuccess", true);
        return "redirect:/profile/orders";
    }

    private List<CartItem> loadCartItems(Cart cart) {
        if (cart == null) return Collections.emptyList();
        return cartItemRepository.findByCart(cart);
    }

    private User requireUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }
}


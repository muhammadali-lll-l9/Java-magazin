package com.example.internet_shop.controller;

import com.example.internet_shop.entity.Cart;
import com.example.internet_shop.entity.Category;
import com.example.internet_shop.entity.Product;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.entity.Order;
import com.example.internet_shop.entity.OrderItem;
import com.example.internet_shop.repository.CartRepository;
import com.example.internet_shop.repository.CartItemRepository;
import com.example.internet_shop.repository.CategoryRepository;
import com.example.internet_shop.repository.ProductRepository;
import com.example.internet_shop.repository.UserRepository;
import com.example.internet_shop.repository.OrderRepository;
import com.example.internet_shop.repository.OrderItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public AdminController(ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           CartRepository cartRepository,
                           CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(required = false) String section) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("activeSection", section != null ? section : "products");
        return "admin/dashboard";
    }

    @GetMapping("/orders/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "admin/order-details";
    }

    @PostMapping("/orders/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(defaultValue = "orders") String section) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/admin/dashboard?section=" + section;
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                             @RequestParam(defaultValue = "products") String section) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            product.setCategory(null);
        } else {
            categoryRepository.findById(product.getCategory().getId()).ifPresent(product::setCategory);
        }
        if (product.getImageUrl() != null && product.getImageUrl().isBlank()) {
            product.setImageUrl(null);
        }
        productRepository.save(product);
        return "redirect:/admin/dashboard?section=" + section;
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model, @RequestParam(defaultValue = "products") String section) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("activeSection", section);
        return "admin/dashboard";
    }

    @GetMapping("/products/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id, @RequestParam(defaultValue = "products") String section) {
        orderItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
        return "redirect:/admin/dashboard?section=" + section;
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id, @RequestParam String name, @RequestParam(defaultValue = "categories") String section) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        categoryRepository.save(category);
        return "redirect:/admin/dashboard?section=" + section;
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, @RequestParam(defaultValue = "categories") String section) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/dashboard?section=" + section;
    }

    @GetMapping("/users/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id, @RequestParam(defaultValue = "users") String section) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            Cart cart = cartRepository.findByUser(user).orElse(null);
            if (cart != null) {
                cartItemRepository.deleteByCartId(cart.getId());
                cartRepository.delete(cart);
            }
            List<Order> orders = orderRepository.findByUser(user);
            for (Order order : orders) {
                orderItemRepository.deleteByOrderId(order.getId());
                orderRepository.delete(order);
            }
            userRepository.delete(user);
        }
        return "redirect:/admin/dashboard?section=" + section;
    }

    @GetMapping("/orders/delete/{id}")
    @Transactional
    public String deleteOrder(@PathVariable Long id, @RequestParam(defaultValue = "orders") String section) {
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
        return "redirect:/admin/dashboard?section=" + section;
    }
}



package com.example.internet_shop.controller;

import com.example.internet_shop.entity.Product;
import com.example.internet_shop.entity.User;
import com.example.internet_shop.repository.ProductRepository;
import com.example.internet_shop.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getAllProducts(@RequestParam(required = false) Long categoryId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findAll().stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .toList();
        } else {
            products = productRepository.findAll();
        }
        model.addAttribute("products", products);

        boolean buyerCabinet = false;
        if (userDetails != null) {
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                buyerCabinet = true;
                userRepository.findByUsername(userDetails.getUsername())
                        .ifPresent(user -> model.addAttribute("user", user));
            }
        }
        model.addAttribute("buyerCabinet", buyerCabinet);

        return "products";
    }
}


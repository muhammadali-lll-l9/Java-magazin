package com.example.internet_shop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) Boolean cart,
                        HttpSession session,
                        Model model) {
        boolean showCartHint = Boolean.TRUE.equals(cart)
                || session.getAttribute("pendingProductId") != null;
        model.addAttribute("showCartHint", showCartHint);
        return "auth/login";
    }
}


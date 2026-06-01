package com.example.internet_shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private ProductController productController;

    @Test
    void contextLoads() {
        assertThat(productController).isNotNull();
    }

    @Test
    void getAllProducts_ShouldReturnProducts() {
        Model model = new BindingAwareModelMap();
        String result = productController.getAllProducts(null, null, model);
        assertThat(result).isEqualTo("products");
    }
}
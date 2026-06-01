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
class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;

    @Test
    void contextLoads() {
        assertThat(categoryController).isNotNull();
    }

    @Test
    void getAllCategories_ShouldReturnCategories() {
        Model model = new BindingAwareModelMap();
        String result = categoryController.getAllCategories(model);
        assertThat(result).isEqualTo("categories");
    }
}
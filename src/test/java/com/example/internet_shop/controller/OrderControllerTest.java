package com.example.internet_shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @Test
    void contextLoads() {
        assertThat(orderController).isNotNull();
    }

    @Test
    void viewOrders_ShouldReturnOrders() {
        String result = orderController.viewOrders();
        assertThat(result).isEqualTo("orders");
    }
}
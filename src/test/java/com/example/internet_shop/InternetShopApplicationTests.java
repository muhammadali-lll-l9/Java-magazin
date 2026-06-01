package com.example.internet_shop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class InternetShopApplicationTest {

    @Test
    void contextLoads() {
        assertThat(InternetShopApplication.class).isNotNull();
    }

    @Test
    void mainMethod_ShouldExist() {
        // Проверяем, что класс можно загрузить
        assertThat(InternetShopApplication.class.getMethods()).isNotEmpty();
    }
}


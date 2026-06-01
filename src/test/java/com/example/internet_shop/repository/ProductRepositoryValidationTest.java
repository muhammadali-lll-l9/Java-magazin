package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Category;
import com.example.internet_shop.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductRepositoryValidationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void savedProduct_ShouldHaveCorrectValues() {
        Category category = new Category("Books", "Literature");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Java Programming");
        product.setDescription("Learn Java");
        product.setPrice(BigDecimal.valueOf(49.99));
        product.setStock(100);
        product.setCategory(category);
        product.setImageUrl("http://example.com/image.jpg");

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Java Programming");
        assertThat(saved.getDescription()).isEqualTo("Learn Java");
        assertThat(saved.getPrice()).isEqualByComparingTo("49.99");
        assertThat(saved.getStock()).isEqualTo(100);
        assertThat(saved.getImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(saved.getCategory().getName()).isEqualTo("Books");
    }
}


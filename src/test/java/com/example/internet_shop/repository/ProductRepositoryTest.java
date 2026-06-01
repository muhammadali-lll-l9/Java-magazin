package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Category;
import com.example.internet_shop.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Electronics", "Devices");
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void saveAndFindProduct_ShouldWork() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(5);
        product.setCategory(testCategory);

        Product saved = productRepository.save(product);
        assertThat(saved.getId()).isNotNull();

        java.util.Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void findByCategoryId_ShouldReturnProducts() {
        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setPrice(BigDecimal.valueOf(1000));
        product1.setStock(5);
        product1.setCategory(testCategory);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Phone");
        product2.setPrice(BigDecimal.valueOf(500));
        product2.setStock(10);
        product2.setCategory(testCategory);
        productRepository.save(product2);

        List<Product> products = productRepository.findByCategoryId(testCategory.getId());
        assertThat(products).hasSize(2);
    }
}


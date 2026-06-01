package com.example.internet_shop.config;

import com.example.internet_shop.entity.*;
import com.example.internet_shop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        System.out.println("🌱 Загрузка начальных данных...");

        // ===== РОЛИ =====
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
            System.out.println("✅ Роли созданы");
        }

        // ===== АДМИНИСТРАТОР =====
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@mail.ru");
            // Находим роль ADMIN
            for (Role r : roleRepository.findAll()) {
                if ("ADMIN".equals(r.getName())) {
                    admin.setRole(r);
                    break;
                }
            }
            userRepository.save(admin);
            System.out.println("✅ Администратор создан (admin/admin123)");
        }


        // ===== КАТЕГОРИИ =====
        if (categoryRepository.count() == 0) {
            Category men = new Category();
            men.setName("Мужское");
            men.setDescription("Одежда для мужчин");
            categoryRepository.save(men);

            Category women = new Category();
            women.setName("Женское");
            women.setDescription("Одежда для женщин");
            categoryRepository.save(women);

            Category kids = new Category();
            kids.setName("Детское");
            kids.setDescription("Одежда для детей");
            categoryRepository.save(kids);

            Category news = new Category();
            news.setName("Новинки");
            news.setDescription("Новинки сезона");
            categoryRepository.save(news);

            Category accessories = new Category();
            accessories.setName("Аксессуары");
            accessories.setDescription("Сумки, часы, украшения");
            categoryRepository.save(accessories);

            Category sale = new Category();
            sale.setName("Скидки");
            sale.setDescription("Товары со скидкой");
            categoryRepository.save(sale);

            System.out.println("✅ Категории созданы");
        }


        // ===== ТОВАРЫ =====
        // ===== ТОВАРЫ =====
        if (productRepository.count() == 0) {
            // Находим категории
            Category men = null;
            Category women = null;
            Category kids = null;
            Category news = null;
            Category accessories = null;
            Category sale = null;

            for (Category c : categoryRepository.findAll()) {
                if ("Мужское".equals(c.getName())) men = c;
                if ("Женское".equals(c.getName())) women = c;
                if ("Детское".equals(c.getName())) kids = c;
                if ("Новинки".equals(c.getName())) news = c;
                if ("Аксессуары".equals(c.getName())) accessories = c;
                if ("Скидки".equals(c.getName())) sale = c;
            }

            // Мужское
            if (men != null) {
                Product p1 = new Product();
                p1.setName("Мужская футболка");
                p1.setPrice(BigDecimal.valueOf(1990));
                p1.setStock(50);
                p1.setCategory(men);
                p1.setImageUrl("/images/men-tshirt.jpg");
                productRepository.save(p1);
            }

            // Женское
            if (women != null) {
                Product p2 = new Product();
                p2.setName("Женское платье");
                p2.setPrice(BigDecimal.valueOf(3990));
                p2.setStock(40);
                p2.setCategory(women);
                p2.setImageUrl("/images/women-dress.jpg");
                productRepository.save(p2);
            }

            // Детское
            if (kids != null) {
                Product p3 = new Product();
                p3.setName("Детская футболка");
                p3.setPrice(BigDecimal.valueOf(1290));
                p3.setStock(60);
                p3.setCategory(kids);
                p3.setImageUrl("/images/kids-tshirt.jpg");
                productRepository.save(p3);
            }

            // Новинки
            if (news != null) {
                Product p4 = new Product();
                p4.setName("Коллекция весна 2026");
                p4.setPrice(BigDecimal.valueOf(2990));
                p4.setStock(100);
                p4.setCategory(news);
                p4.setImageUrl("/images/new-spring.jpg");
                productRepository.save(p4);
            }

            // Аксессуары
            if (accessories != null) {
                Product p5 = new Product();
                p5.setName("Наручные часы");
                p5.setPrice(BigDecimal.valueOf(7990));
                p5.setStock(15);
                p5.setCategory(accessories);
                p5.setImageUrl("/images/watch.jpg");
                productRepository.save(p5);
            }

            // Скидки
            if (sale != null) {
                Product p6 = new Product();
                p6.setName("Футболка со скидкой");
                p6.setPrice(BigDecimal.valueOf(999));
                p6.setStock(30);
                p6.setCategory(sale);
                p6.setImageUrl("/images/sale-tshirt.jpg");
                productRepository.save(p6);
            }

            System.out.println("✅ Товары добавлены");
        }


        // ===== ОБЫЧНЫЙ ПОЛЬЗОВАТЕЛЬ =====
        boolean userExists = false;
        for (User u : userRepository.findAll()) {
            if ("user".equals(u.getUsername())) {
                userExists = true;
                break;
            }
        }

        if (!userExists && roleRepository.count() > 1) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setEmail("user@mail.ru");
            // Находим роль USER
            for (Role r : roleRepository.findAll()) {
                if ("USER".equals(r.getName())) {
                    regularUser.setRole(r);
                    break;
                }
            }
            userRepository.save(regularUser);
            System.out.println("✅ Обычный пользователь создан (user/user123)");
        }

        System.out.println("✅ Начальные данные загружены!");
    }
}
package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Cart;
import com.example.internet_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
package com.example.internet_shop.repository;

import com.example.internet_shop.entity.Order;
import com.example.internet_shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    void deleteByOrderId(Long orderId);
    void deleteByProductId(Long productId);
}

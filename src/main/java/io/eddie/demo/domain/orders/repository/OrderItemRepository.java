package io.eddie.demo.domain.orders.repository;

import io.eddie.demo.domain.orders.model.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
    select
        oi
    from
        OrderItem oi
    join
        oi.order o
    where
        oi.createdAt between :startDate and :endDate
    and
        o.orderState = 'PAID'
    """)
    Page<OrderItem> findAllEligibleSettlements(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


}

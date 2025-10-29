package io.eddie.demo.domain.orders.service;

import io.eddie.demo.domain.carts.model.entity.CartItem;
import io.eddie.demo.domain.carts.service.CartService;
import io.eddie.demo.domain.orders.model.entity.OrderItem;
import io.eddie.demo.domain.orders.model.entity.Orders;
import io.eddie.demo.domain.orders.model.vo.CreateOrderRequest;
import io.eddie.demo.domain.orders.model.vo.OrderState;
import io.eddie.demo.domain.orders.repository.OrderItemRepository;
import io.eddie.demo.domain.orders.repository.OrderRepository;
import io.eddie.demo.domain.payments.model.dto.PaymentRequest;
import io.eddie.demo.domain.payments.model.entity.Payment;
import io.eddie.demo.domain.payments.model.vo.PaymentType;
import io.eddie.demo.domain.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // TEMP
    private final CartService cartService;

    @Override
    @Transactional
    public Orders createOrder(String accountCode, CreateOrderRequest request) {

        Orders order = Orders.builder()
                .accountCode(accountCode)
                .build();

        List<CartItem> targetItems = cartService.getItemsByCodes(request.cartItemCodes());

        if ( targetItems.isEmpty() )
            throw new IllegalStateException("선택된 아이템이 없습니다");

        List<OrderItem> orderItems = targetItems.stream()
                .map(i -> {
                    OrderItem item = OrderItem.builder()
                            .sellerCode(i.getSellerCode())
                            .productCode(i.getProductCode())
                            .productName(i.getProductName())
                            .productPrice(i.getProductPrice())
                            .quantity(i.getQuantity())
                            .build();

                    order.addItem(item);
                    return item;
                })
                .toList();

        orderRepository.save(order);

        orderItemRepository.saveAll(orderItems);
        cartService.deleteItemsByCodes(request.cartItemCodes());

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Orders getOrder(String accountCode, String orderCode) {
        return orderRepository.findByAccountCodeAndCode(accountCode, orderCode)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    @Override
    public Orders getOrderByCode(String code) {
        return orderRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public Orders completeOrder(String accountCode, String orderCode) {

        Orders targetOrder = getOrder(accountCode, orderCode);
        targetOrder.complete();

        return targetOrder;
    }

    @Override
    @Transactional
    public Orders cancelOrder(String accountCode, String orderCode) {

        Orders order = getOrder(accountCode, orderCode);

        if ( order.getOrderState() == OrderState.CANCELLED ) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        if ( order.getOrderState() == OrderState.PAID ) {
            throw new IllegalStateException("결제가 완료된 주문입니다. 환불신청을 해주시기 바랍니다.");
        }

        order.cancel();

        return order;

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Orders> getOrders(String accountCode, Pageable pageable) {
        return orderRepository.findAllByAccountCode(accountCode, pageable);
    }

    @Override
    public Slice<OrderItem> findAllEligibleSettlements(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderItemRepository.findAllEligibleSettlements(startDate, endDate, pageable);
    }

}


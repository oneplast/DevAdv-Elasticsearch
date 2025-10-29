package io.eddie.demo.domain.settlements.service;

import io.eddie.demo.domain.deposits.model.entity.DepositHistory;
import io.eddie.demo.domain.deposits.service.DepositService;
import io.eddie.demo.domain.orders.model.entity.OrderItem;
import io.eddie.demo.domain.orders.service.OrderService;
import io.eddie.demo.domain.settlements.model.entity.Settlement;
import io.eddie.demo.domain.settlements.model.vo.SettlementStatus;
import io.eddie.demo.domain.settlements.repository.SettlementRepository;
import io.eddie.demo.domain.settlements.utils.SettlementTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    @Value("${custom.settlement.rate}")
    private Double settlementRate;

    private final DepositService depositService;
    private final OrderService orderService;

    private final SettlementRepository settlementRepository;

    @Override
    @Transactional
    public List<Settlement> prepare(String dateStr) {

        Slice<OrderItem> eligibleItems = orderService.findAllEligibleSettlements(
                SettlementTimeUtils.getStartDay(dateStr),
                SettlementTimeUtils.getEndDay(dateStr),
                null
        );

        return eligibleItems.stream()
                .map(i -> {

                    Long productPrice = i.getProductPrice();

                    Settlement settlement = Settlement.builder()
                            .buyerCode(i.getOrder().getAccountCode())
                            .sellerCode(i.getSellerCode())
                            .orderItemCode(i.getCode())
                            .settlementRate(settlementRate)
                            .totalAmount(productPrice)
                            .build();

                    settlement.applySettlementPolicy();

                    return settlementRepository.save(settlement);

                })
                .toList();

    }

    @Override
    @Transactional
    public List<Settlement> process(String dateStr) {

        List<Settlement> targetSettlements = settlementRepository.findTargetSettlements(
                SettlementTimeUtils.getStartDay(dateStr),
                SettlementTimeUtils.getEndDay(dateStr)
        );

        return targetSettlements.stream()
                .peek(s -> {

                    Long amount = s.getSettlementBalance();
                    depositService.charge(s.getSellerCode(), DepositHistory.DepositHistoryType.SETTLEMENT, amount);

                    s.done();

                })
                .toList();

    }

    @Override
    public boolean isProcessable(SettlementStatus status) {
        return status == SettlementStatus.SETTLEMENT_CREATED;
    }

}

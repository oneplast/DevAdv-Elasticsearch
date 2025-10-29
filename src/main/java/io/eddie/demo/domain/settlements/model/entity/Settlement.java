package io.eddie.demo.domain.settlements.model.entity;


import io.eddie.core.model.persistence.BaseEntity;
import io.eddie.demo.domain.settlements.model.vo.SettlementStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

    private String buyerCode;
    private String sellerCode;

    private String orderItemCode;

    @Enumerated(EnumType.STRING)
    private SettlementStatus settlementStatus = SettlementStatus.SETTLEMENT_CREATED;

    // 정산 일자
    private LocalDateTime settlementDate;

    // 정산 당시 정산 비율
    private Double settlementRate;

    // 물품 총액
    private Long totalAmount;

    // 정산 수수료
    private Long settlementAmount;

    // 정산 잔액
    private Long settlementBalance;

    @Builder
    public Settlement(String buyerCode, String sellerCode, String orderItemCode, LocalDateTime settlementDate, Double settlementRate, Long totalAmount) {
        this.buyerCode = buyerCode;
        this.sellerCode = sellerCode;
        this.orderItemCode = orderItemCode;
        this.settlementDate = settlementDate;
        this.settlementRate = settlementRate;
        this.totalAmount = totalAmount;
    }

    public void process() {
        this.settlementStatus = SettlementStatus.SETTLEMENT_PROCESSING;
    }

    public void done() {
        this.settlementStatus = SettlementStatus.SETTLEMENT_SUCCESS;
        this.settlementDate = LocalDateTime.now();
    }

    public void fail() {
        this.settlementStatus = SettlementStatus.SETTLEMENT_FAILED;
    }

    private long calcSettlementAmount() {
        return (long) this.totalAmount - calcSettlementBalance();
    }

    private long calcSettlementBalance() {
        return (long) (this.totalAmount * this.settlementRate);
    }

    public void applySettlementPolicy() {
        this.settlementBalance = calcSettlementBalance();
        this.settlementAmount = calcSettlementAmount();
    }

}

package io.eddie.demo.domain.settlements.repository;

import io.eddie.demo.domain.settlements.model.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByCode(String code);

    @Query("""
    select
        s
    from
        Settlement s
    where
        s.createdAt between :startDate and :endDate
    and
        s.settlementStatus = 'SETTLEMENT_CREATED'
    """)
    List<Settlement> findTargetSettlements(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}

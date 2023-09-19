package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FinancialTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FinancialTransaction entity.
 */
@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    default Optional<FinancialTransaction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FinancialTransaction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FinancialTransaction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct financialTransaction from FinancialTransaction financialTransaction left join fetch financialTransaction.wallet",
        countQuery = "select count(distinct financialTransaction) from FinancialTransaction financialTransaction"
    )
    Page<FinancialTransaction> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct financialTransaction from FinancialTransaction financialTransaction left join fetch financialTransaction.wallet"
    )
    List<FinancialTransaction> findAllWithToOneRelationships();

    @Query(
        "select financialTransaction from FinancialTransaction financialTransaction left join fetch financialTransaction.wallet where financialTransaction.id =:id"
    )
    Optional<FinancialTransaction> findOneWithToOneRelationships(@Param("id") Long id);
}

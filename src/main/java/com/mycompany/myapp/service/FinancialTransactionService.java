package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.FinancialTransactionDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.FinancialTransaction}.
 */
public interface FinancialTransactionService {
    /**
     * Save a financialTransaction.
     *
     * @param financialTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    FinancialTransactionDTO save(FinancialTransactionDTO financialTransactionDTO);

    /**
     * Updates a financialTransaction.
     *
     * @param financialTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    FinancialTransactionDTO update(FinancialTransactionDTO financialTransactionDTO);

    /**
     * Partially updates a financialTransaction.
     *
     * @param financialTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FinancialTransactionDTO> partialUpdate(FinancialTransactionDTO financialTransactionDTO);

    /**
     * Get all the financialTransactions.
     *
     * @return the list of entities.
     */
    List<FinancialTransactionDTO> findAll();

    /**
     * Get all the financialTransactions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FinancialTransactionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" financialTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FinancialTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" financialTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

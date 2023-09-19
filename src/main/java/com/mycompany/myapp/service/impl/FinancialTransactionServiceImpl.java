package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.FinancialTransaction;
import com.mycompany.myapp.repository.FinancialTransactionRepository;
import com.mycompany.myapp.service.FinancialTransactionService;
import com.mycompany.myapp.service.dto.FinancialTransactionDTO;
import com.mycompany.myapp.service.mapper.FinancialTransactionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FinancialTransaction}.
 */
@Service
@Transactional
public class FinancialTransactionServiceImpl implements FinancialTransactionService {

    private final Logger log = LoggerFactory.getLogger(FinancialTransactionServiceImpl.class);

    private final FinancialTransactionRepository financialTransactionRepository;

    private final FinancialTransactionMapper financialTransactionMapper;

    public FinancialTransactionServiceImpl(
        FinancialTransactionRepository financialTransactionRepository,
        FinancialTransactionMapper financialTransactionMapper
    ) {
        this.financialTransactionRepository = financialTransactionRepository;
        this.financialTransactionMapper = financialTransactionMapper;
    }

    @Override
    public FinancialTransactionDTO save(FinancialTransactionDTO financialTransactionDTO) {
        log.debug("Request to save FinancialTransaction : {}", financialTransactionDTO);
        FinancialTransaction financialTransaction = financialTransactionMapper.toEntity(financialTransactionDTO);
        financialTransaction = financialTransactionRepository.save(financialTransaction);
        return financialTransactionMapper.toDto(financialTransaction);
    }

    @Override
    public FinancialTransactionDTO update(FinancialTransactionDTO financialTransactionDTO) {
        log.debug("Request to update FinancialTransaction : {}", financialTransactionDTO);
        FinancialTransaction financialTransaction = financialTransactionMapper.toEntity(financialTransactionDTO);
        financialTransaction = financialTransactionRepository.save(financialTransaction);
        return financialTransactionMapper.toDto(financialTransaction);
    }

    @Override
    public Optional<FinancialTransactionDTO> partialUpdate(FinancialTransactionDTO financialTransactionDTO) {
        log.debug("Request to partially update FinancialTransaction : {}", financialTransactionDTO);

        return financialTransactionRepository
            .findById(financialTransactionDTO.getId())
            .map(existingFinancialTransaction -> {
                financialTransactionMapper.partialUpdate(existingFinancialTransaction, financialTransactionDTO);

                return existingFinancialTransaction;
            })
            .map(financialTransactionRepository::save)
            .map(financialTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialTransactionDTO> findAll() {
        log.debug("Request to get all FinancialTransactions");
        return financialTransactionRepository
            .findAll()
            .stream()
            .map(financialTransactionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<FinancialTransactionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return financialTransactionRepository.findAllWithEagerRelationships(pageable).map(financialTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialTransactionDTO> findOne(Long id) {
        log.debug("Request to get FinancialTransaction : {}", id);
        return financialTransactionRepository.findOneWithEagerRelationships(id).map(financialTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete FinancialTransaction : {}", id);
        financialTransactionRepository.deleteById(id);
    }
}

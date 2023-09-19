package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinancialTransactionMapperTest {

    private FinancialTransactionMapper financialTransactionMapper;

    @BeforeEach
    public void setUp() {
        financialTransactionMapper = new FinancialTransactionMapperImpl();
    }
}

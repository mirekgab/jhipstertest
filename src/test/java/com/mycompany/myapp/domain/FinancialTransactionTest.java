package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinancialTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinancialTransaction.class);
        FinancialTransaction financialTransaction1 = new FinancialTransaction();
        financialTransaction1.setId(1L);
        FinancialTransaction financialTransaction2 = new FinancialTransaction();
        financialTransaction2.setId(financialTransaction1.getId());
        assertThat(financialTransaction1).isEqualTo(financialTransaction2);
        financialTransaction2.setId(2L);
        assertThat(financialTransaction1).isNotEqualTo(financialTransaction2);
        financialTransaction1.setId(null);
        assertThat(financialTransaction1).isNotEqualTo(financialTransaction2);
    }
}

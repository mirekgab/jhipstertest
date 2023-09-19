package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.FinancialTransaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FinancialTransactionDTO implements Serializable {

    private Long id;

    private BigDecimal amount;

    private String description;

    private WalletDTO wallet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(WalletDTO wallet) {
        this.wallet = wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinancialTransactionDTO)) {
            return false;
        }

        FinancialTransactionDTO financialTransactionDTO = (FinancialTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, financialTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinancialTransactionDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", description='" + getDescription() + "'" +
            ", wallet=" + getWallet() +
            "}";
    }
}

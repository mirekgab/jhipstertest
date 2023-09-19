package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.FinancialTransaction;
import com.mycompany.myapp.domain.Wallet;
import com.mycompany.myapp.service.dto.FinancialTransactionDTO;
import com.mycompany.myapp.service.dto.WalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FinancialTransaction} and its DTO {@link FinancialTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface FinancialTransactionMapper extends EntityMapper<FinancialTransactionDTO, FinancialTransaction> {
    @Mapping(target = "wallet", source = "wallet", qualifiedByName = "walletName")
    FinancialTransactionDTO toDto(FinancialTransaction s);

    @Named("walletName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WalletDTO toDtoWalletName(Wallet wallet);
}

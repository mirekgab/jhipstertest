package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Wallet;
import com.mycompany.myapp.service.dto.WalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {}

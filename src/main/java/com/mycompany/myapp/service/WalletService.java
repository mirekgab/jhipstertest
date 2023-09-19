package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.WalletDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Wallet}.
 */
public interface WalletService {
    /**
     * Save a wallet.
     *
     * @param walletDTO the entity to save.
     * @return the persisted entity.
     */
    WalletDTO save(WalletDTO walletDTO);

    /**
     * Updates a wallet.
     *
     * @param walletDTO the entity to update.
     * @return the persisted entity.
     */
    WalletDTO update(WalletDTO walletDTO);

    /**
     * Partially updates a wallet.
     *
     * @param walletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WalletDTO> partialUpdate(WalletDTO walletDTO);

    /**
     * Get all the wallets.
     *
     * @return the list of entities.
     */
    List<WalletDTO> findAll();

    /**
     * Get the "id" wallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WalletDTO> findOne(Long id);

    /**
     * Delete the "id" wallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

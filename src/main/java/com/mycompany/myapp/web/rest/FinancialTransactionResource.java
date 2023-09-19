package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FinancialTransactionRepository;
import com.mycompany.myapp.service.FinancialTransactionService;
import com.mycompany.myapp.service.dto.FinancialTransactionDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.FinancialTransaction}.
 */
@RestController
@RequestMapping("/api")
public class FinancialTransactionResource {

    private final Logger log = LoggerFactory.getLogger(FinancialTransactionResource.class);

    private static final String ENTITY_NAME = "financialTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinancialTransactionService financialTransactionService;

    private final FinancialTransactionRepository financialTransactionRepository;

    public FinancialTransactionResource(
        FinancialTransactionService financialTransactionService,
        FinancialTransactionRepository financialTransactionRepository
    ) {
        this.financialTransactionService = financialTransactionService;
        this.financialTransactionRepository = financialTransactionRepository;
    }

    /**
     * {@code POST  /financial-transactions} : Create a new financialTransaction.
     *
     * @param financialTransactionDTO the financialTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new financialTransactionDTO, or with status {@code 400 (Bad Request)} if the financialTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/financial-transactions")
    public ResponseEntity<FinancialTransactionDTO> createFinancialTransaction(@RequestBody FinancialTransactionDTO financialTransactionDTO)
        throws URISyntaxException {
        log.debug("REST request to save FinancialTransaction : {}", financialTransactionDTO);
        if (financialTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new financialTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FinancialTransactionDTO result = financialTransactionService.save(financialTransactionDTO);
        return ResponseEntity
            .created(new URI("/api/financial-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /financial-transactions/:id} : Updates an existing financialTransaction.
     *
     * @param id the id of the financialTransactionDTO to save.
     * @param financialTransactionDTO the financialTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financialTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the financialTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the financialTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/financial-transactions/{id}")
    public ResponseEntity<FinancialTransactionDTO> updateFinancialTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FinancialTransactionDTO financialTransactionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FinancialTransaction : {}, {}", id, financialTransactionDTO);
        if (financialTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financialTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financialTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FinancialTransactionDTO result = financialTransactionService.update(financialTransactionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, financialTransactionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /financial-transactions/:id} : Partial updates given fields of an existing financialTransaction, field will ignore if it is null
     *
     * @param id the id of the financialTransactionDTO to save.
     * @param financialTransactionDTO the financialTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financialTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the financialTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the financialTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the financialTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/financial-transactions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinancialTransactionDTO> partialUpdateFinancialTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FinancialTransactionDTO financialTransactionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FinancialTransaction partially : {}, {}", id, financialTransactionDTO);
        if (financialTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financialTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financialTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinancialTransactionDTO> result = financialTransactionService.partialUpdate(financialTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, financialTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /financial-transactions} : get all the financialTransactions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of financialTransactions in body.
     */
    @GetMapping("/financial-transactions")
    public List<FinancialTransactionDTO> getAllFinancialTransactions(
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get all FinancialTransactions");
        return financialTransactionService.findAll();
    }

    /**
     * {@code GET  /financial-transactions/:id} : get the "id" financialTransaction.
     *
     * @param id the id of the financialTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the financialTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/financial-transactions/{id}")
    public ResponseEntity<FinancialTransactionDTO> getFinancialTransaction(@PathVariable Long id) {
        log.debug("REST request to get FinancialTransaction : {}", id);
        Optional<FinancialTransactionDTO> financialTransactionDTO = financialTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(financialTransactionDTO);
    }

    /**
     * {@code DELETE  /financial-transactions/:id} : delete the "id" financialTransaction.
     *
     * @param id the id of the financialTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/financial-transactions/{id}")
    public ResponseEntity<Void> deleteFinancialTransaction(@PathVariable Long id) {
        log.debug("REST request to delete FinancialTransaction : {}", id);
        financialTransactionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

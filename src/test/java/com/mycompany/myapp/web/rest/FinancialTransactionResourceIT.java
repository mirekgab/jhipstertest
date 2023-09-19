package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.FinancialTransaction;
import com.mycompany.myapp.repository.FinancialTransactionRepository;
import com.mycompany.myapp.service.FinancialTransactionService;
import com.mycompany.myapp.service.dto.FinancialTransactionDTO;
import com.mycompany.myapp.service.mapper.FinancialTransactionMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FinancialTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FinancialTransactionResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/financial-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private FinancialTransactionRepository financialTransactionRepositoryMock;

    @Autowired
    private FinancialTransactionMapper financialTransactionMapper;

    @Mock
    private FinancialTransactionService financialTransactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFinancialTransactionMockMvc;

    private FinancialTransaction financialTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinancialTransaction createEntity(EntityManager em) {
        FinancialTransaction financialTransaction = new FinancialTransaction().amount(DEFAULT_AMOUNT).description(DEFAULT_DESCRIPTION);
        return financialTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinancialTransaction createUpdatedEntity(EntityManager em) {
        FinancialTransaction financialTransaction = new FinancialTransaction().amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);
        return financialTransaction;
    }

    @BeforeEach
    public void initTest() {
        financialTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createFinancialTransaction() throws Exception {
        int databaseSizeBeforeCreate = financialTransactionRepository.findAll().size();
        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);
        restFinancialTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        FinancialTransaction testFinancialTransaction = financialTransactionList.get(financialTransactionList.size() - 1);
        assertThat(testFinancialTransaction.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testFinancialTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createFinancialTransactionWithExistingId() throws Exception {
        // Create the FinancialTransaction with an existing ID
        financialTransaction.setId(1L);
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        int databaseSizeBeforeCreate = financialTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinancialTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFinancialTransactions() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        // Get all the financialTransactionList
        restFinancialTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financialTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFinancialTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(financialTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFinancialTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(financialTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFinancialTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(financialTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFinancialTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(financialTransactionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFinancialTransaction() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        // Get the financialTransaction
        restFinancialTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, financialTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financialTransaction.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingFinancialTransaction() throws Exception {
        // Get the financialTransaction
        restFinancialTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFinancialTransaction() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();

        // Update the financialTransaction
        FinancialTransaction updatedFinancialTransaction = financialTransactionRepository.findById(financialTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedFinancialTransaction are not directly saved in db
        em.detach(updatedFinancialTransaction);
        updatedFinancialTransaction.amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(updatedFinancialTransaction);

        restFinancialTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financialTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
        FinancialTransaction testFinancialTransaction = financialTransactionList.get(financialTransactionList.size() - 1);
        assertThat(testFinancialTransaction.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testFinancialTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financialTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFinancialTransactionWithPatch() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();

        // Update the financialTransaction using partial update
        FinancialTransaction partialUpdatedFinancialTransaction = new FinancialTransaction();
        partialUpdatedFinancialTransaction.setId(financialTransaction.getId());

        partialUpdatedFinancialTransaction.description(UPDATED_DESCRIPTION);

        restFinancialTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinancialTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinancialTransaction))
            )
            .andExpect(status().isOk());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
        FinancialTransaction testFinancialTransaction = financialTransactionList.get(financialTransactionList.size() - 1);
        assertThat(testFinancialTransaction.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testFinancialTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateFinancialTransactionWithPatch() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();

        // Update the financialTransaction using partial update
        FinancialTransaction partialUpdatedFinancialTransaction = new FinancialTransaction();
        partialUpdatedFinancialTransaction.setId(financialTransaction.getId());

        partialUpdatedFinancialTransaction.amount(UPDATED_AMOUNT).description(UPDATED_DESCRIPTION);

        restFinancialTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinancialTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinancialTransaction))
            )
            .andExpect(status().isOk());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
        FinancialTransaction testFinancialTransaction = financialTransactionList.get(financialTransactionList.size() - 1);
        assertThat(testFinancialTransaction.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testFinancialTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financialTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFinancialTransaction() throws Exception {
        int databaseSizeBeforeUpdate = financialTransactionRepository.findAll().size();
        financialTransaction.setId(count.incrementAndGet());

        // Create the FinancialTransaction
        FinancialTransactionDTO financialTransactionDTO = financialTransactionMapper.toDto(financialTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinancialTransaction in the database
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFinancialTransaction() throws Exception {
        // Initialize the database
        financialTransactionRepository.saveAndFlush(financialTransaction);

        int databaseSizeBeforeDelete = financialTransactionRepository.findAll().size();

        // Delete the financialTransaction
        restFinancialTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, financialTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinancialTransaction> financialTransactionList = financialTransactionRepository.findAll();
        assertThat(financialTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

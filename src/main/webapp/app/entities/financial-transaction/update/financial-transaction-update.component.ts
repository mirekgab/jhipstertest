import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FinancialTransactionFormService, FinancialTransactionFormGroup } from './financial-transaction-form.service';
import { IFinancialTransaction } from '../financial-transaction.model';
import { FinancialTransactionService } from '../service/financial-transaction.service';
import { IWallet } from 'app/entities/wallet/wallet.model';
import { WalletService } from 'app/entities/wallet/service/wallet.service';

@Component({
  selector: 'jhi-financial-transaction-update',
  templateUrl: './financial-transaction-update.component.html',
})
export class FinancialTransactionUpdateComponent implements OnInit {
  isSaving = false;
  financialTransaction: IFinancialTransaction | null = null;

  walletsSharedCollection: IWallet[] = [];

  editForm: FinancialTransactionFormGroup = this.financialTransactionFormService.createFinancialTransactionFormGroup();

  constructor(
    protected financialTransactionService: FinancialTransactionService,
    protected financialTransactionFormService: FinancialTransactionFormService,
    protected walletService: WalletService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareWallet = (o1: IWallet | null, o2: IWallet | null): boolean => this.walletService.compareWallet(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ financialTransaction }) => {
      this.financialTransaction = financialTransaction;
      if (financialTransaction) {
        this.updateForm(financialTransaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const financialTransaction = this.financialTransactionFormService.getFinancialTransaction(this.editForm);
    if (financialTransaction.id !== null) {
      this.subscribeToSaveResponse(this.financialTransactionService.update(financialTransaction));
    } else {
      this.subscribeToSaveResponse(this.financialTransactionService.create(financialTransaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFinancialTransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(financialTransaction: IFinancialTransaction): void {
    this.financialTransaction = financialTransaction;
    this.financialTransactionFormService.resetForm(this.editForm, financialTransaction);

    this.walletsSharedCollection = this.walletService.addWalletToCollectionIfMissing<IWallet>(
      this.walletsSharedCollection,
      financialTransaction.wallet
    );
  }

  protected loadRelationshipsOptions(): void {
    this.walletService
      .query()
      .pipe(map((res: HttpResponse<IWallet[]>) => res.body ?? []))
      .pipe(
        map((wallets: IWallet[]) => this.walletService.addWalletToCollectionIfMissing<IWallet>(wallets, this.financialTransaction?.wallet))
      )
      .subscribe((wallets: IWallet[]) => (this.walletsSharedCollection = wallets));
  }
}

import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFinancialTransaction, NewFinancialTransaction } from '../financial-transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFinancialTransaction for edit and NewFinancialTransactionFormGroupInput for create.
 */
type FinancialTransactionFormGroupInput = IFinancialTransaction | PartialWithRequiredKeyOf<NewFinancialTransaction>;

type FinancialTransactionFormDefaults = Pick<NewFinancialTransaction, 'id'>;

type FinancialTransactionFormGroupContent = {
  id: FormControl<IFinancialTransaction['id'] | NewFinancialTransaction['id']>;
  amount: FormControl<IFinancialTransaction['amount']>;
  description: FormControl<IFinancialTransaction['description']>;
  wallet: FormControl<IFinancialTransaction['wallet']>;
};

export type FinancialTransactionFormGroup = FormGroup<FinancialTransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FinancialTransactionFormService {
  createFinancialTransactionFormGroup(
    financialTransaction: FinancialTransactionFormGroupInput = { id: null }
  ): FinancialTransactionFormGroup {
    const financialTransactionRawValue = {
      ...this.getFormDefaults(),
      ...financialTransaction,
    };
    return new FormGroup<FinancialTransactionFormGroupContent>({
      id: new FormControl(
        { value: financialTransactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      amount: new FormControl(financialTransactionRawValue.amount),
      description: new FormControl(financialTransactionRawValue.description),
      wallet: new FormControl(financialTransactionRawValue.wallet),
    });
  }

  getFinancialTransaction(form: FinancialTransactionFormGroup): IFinancialTransaction | NewFinancialTransaction {
    return form.getRawValue() as IFinancialTransaction | NewFinancialTransaction;
  }

  resetForm(form: FinancialTransactionFormGroup, financialTransaction: FinancialTransactionFormGroupInput): void {
    const financialTransactionRawValue = { ...this.getFormDefaults(), ...financialTransaction };
    form.reset(
      {
        ...financialTransactionRawValue,
        id: { value: financialTransactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FinancialTransactionFormDefaults {
    return {
      id: null,
    };
  }
}

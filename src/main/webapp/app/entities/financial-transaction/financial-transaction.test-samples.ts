import { IFinancialTransaction, NewFinancialTransaction } from './financial-transaction.model';

export const sampleWithRequiredData: IFinancialTransaction = {
  id: 31041,
};

export const sampleWithPartialData: IFinancialTransaction = {
  id: 86304,
  description: 'Granite navigating',
};

export const sampleWithFullData: IFinancialTransaction = {
  id: 89736,
  amount: 47084,
  description: 'Frozen open-source',
};

export const sampleWithNewData: NewFinancialTransaction = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

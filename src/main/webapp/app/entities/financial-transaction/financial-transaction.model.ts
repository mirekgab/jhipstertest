import { IWallet } from 'app/entities/wallet/wallet.model';

export interface IFinancialTransaction {
  id: number;
  amount?: number | null;
  description?: string | null;
  wallet?: Pick<IWallet, 'id' | 'name'> | null;
}

export type NewFinancialTransaction = Omit<IFinancialTransaction, 'id'> & { id: null };

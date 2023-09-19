export interface IWallet {
  id: number;
  name?: string | null;
}

export type NewWallet = Omit<IWallet, 'id'> & { id: null };

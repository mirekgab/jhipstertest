import { IWallet, NewWallet } from './wallet.model';

export const sampleWithRequiredData: IWallet = {
  id: 89141,
};

export const sampleWithPartialData: IWallet = {
  id: 11235,
  name: 'Sleek',
};

export const sampleWithFullData: IWallet = {
  id: 25084,
  name: 'bleeding-edge',
};

export const sampleWithNewData: NewWallet = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

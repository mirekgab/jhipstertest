import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'wallet',
        data: { pageTitle: 'Wallets' },
        loadChildren: () => import('./wallet/wallet.module').then(m => m.WalletModule),
      },
      {
        path: 'financial-transaction',
        data: { pageTitle: 'FinancialTransactions' },
        loadChildren: () => import('./financial-transaction/financial-transaction.module').then(m => m.FinancialTransactionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

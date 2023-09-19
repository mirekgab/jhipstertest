import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFinancialTransaction, NewFinancialTransaction } from '../financial-transaction.model';

export type PartialUpdateFinancialTransaction = Partial<IFinancialTransaction> & Pick<IFinancialTransaction, 'id'>;

export type EntityResponseType = HttpResponse<IFinancialTransaction>;
export type EntityArrayResponseType = HttpResponse<IFinancialTransaction[]>;

@Injectable({ providedIn: 'root' })
export class FinancialTransactionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/financial-transactions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(financialTransaction: NewFinancialTransaction): Observable<EntityResponseType> {
    return this.http.post<IFinancialTransaction>(this.resourceUrl, financialTransaction, { observe: 'response' });
  }

  update(financialTransaction: IFinancialTransaction): Observable<EntityResponseType> {
    return this.http.put<IFinancialTransaction>(
      `${this.resourceUrl}/${this.getFinancialTransactionIdentifier(financialTransaction)}`,
      financialTransaction,
      { observe: 'response' }
    );
  }

  partialUpdate(financialTransaction: PartialUpdateFinancialTransaction): Observable<EntityResponseType> {
    return this.http.patch<IFinancialTransaction>(
      `${this.resourceUrl}/${this.getFinancialTransactionIdentifier(financialTransaction)}`,
      financialTransaction,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFinancialTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFinancialTransaction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFinancialTransactionIdentifier(financialTransaction: Pick<IFinancialTransaction, 'id'>): number {
    return financialTransaction.id;
  }

  compareFinancialTransaction(o1: Pick<IFinancialTransaction, 'id'> | null, o2: Pick<IFinancialTransaction, 'id'> | null): boolean {
    return o1 && o2 ? this.getFinancialTransactionIdentifier(o1) === this.getFinancialTransactionIdentifier(o2) : o1 === o2;
  }

  addFinancialTransactionToCollectionIfMissing<Type extends Pick<IFinancialTransaction, 'id'>>(
    financialTransactionCollection: Type[],
    ...financialTransactionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const financialTransactions: Type[] = financialTransactionsToCheck.filter(isPresent);
    if (financialTransactions.length > 0) {
      const financialTransactionCollectionIdentifiers = financialTransactionCollection.map(
        financialTransactionItem => this.getFinancialTransactionIdentifier(financialTransactionItem)!
      );
      const financialTransactionsToAdd = financialTransactions.filter(financialTransactionItem => {
        const financialTransactionIdentifier = this.getFinancialTransactionIdentifier(financialTransactionItem);
        if (financialTransactionCollectionIdentifiers.includes(financialTransactionIdentifier)) {
          return false;
        }
        financialTransactionCollectionIdentifiers.push(financialTransactionIdentifier);
        return true;
      });
      return [...financialTransactionsToAdd, ...financialTransactionCollection];
    }
    return financialTransactionCollection;
  }
}

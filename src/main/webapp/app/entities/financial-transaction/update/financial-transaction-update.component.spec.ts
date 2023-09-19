import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FinancialTransactionFormService } from './financial-transaction-form.service';
import { FinancialTransactionService } from '../service/financial-transaction.service';
import { IFinancialTransaction } from '../financial-transaction.model';
import { IWallet } from 'app/entities/wallet/wallet.model';
import { WalletService } from 'app/entities/wallet/service/wallet.service';

import { FinancialTransactionUpdateComponent } from './financial-transaction-update.component';

describe('FinancialTransaction Management Update Component', () => {
  let comp: FinancialTransactionUpdateComponent;
  let fixture: ComponentFixture<FinancialTransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let financialTransactionFormService: FinancialTransactionFormService;
  let financialTransactionService: FinancialTransactionService;
  let walletService: WalletService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FinancialTransactionUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FinancialTransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FinancialTransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    financialTransactionFormService = TestBed.inject(FinancialTransactionFormService);
    financialTransactionService = TestBed.inject(FinancialTransactionService);
    walletService = TestBed.inject(WalletService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Wallet query and add missing value', () => {
      const financialTransaction: IFinancialTransaction = { id: 456 };
      const wallet: IWallet = { id: 81157 };
      financialTransaction.wallet = wallet;

      const walletCollection: IWallet[] = [{ id: 59144 }];
      jest.spyOn(walletService, 'query').mockReturnValue(of(new HttpResponse({ body: walletCollection })));
      const additionalWallets = [wallet];
      const expectedCollection: IWallet[] = [...additionalWallets, ...walletCollection];
      jest.spyOn(walletService, 'addWalletToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ financialTransaction });
      comp.ngOnInit();

      expect(walletService.query).toHaveBeenCalled();
      expect(walletService.addWalletToCollectionIfMissing).toHaveBeenCalledWith(
        walletCollection,
        ...additionalWallets.map(expect.objectContaining)
      );
      expect(comp.walletsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const financialTransaction: IFinancialTransaction = { id: 456 };
      const wallet: IWallet = { id: 72090 };
      financialTransaction.wallet = wallet;

      activatedRoute.data = of({ financialTransaction });
      comp.ngOnInit();

      expect(comp.walletsSharedCollection).toContain(wallet);
      expect(comp.financialTransaction).toEqual(financialTransaction);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialTransaction>>();
      const financialTransaction = { id: 123 };
      jest.spyOn(financialTransactionFormService, 'getFinancialTransaction').mockReturnValue(financialTransaction);
      jest.spyOn(financialTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financialTransaction }));
      saveSubject.complete();

      // THEN
      expect(financialTransactionFormService.getFinancialTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(financialTransactionService.update).toHaveBeenCalledWith(expect.objectContaining(financialTransaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialTransaction>>();
      const financialTransaction = { id: 123 };
      jest.spyOn(financialTransactionFormService, 'getFinancialTransaction').mockReturnValue({ id: null });
      jest.spyOn(financialTransactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialTransaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financialTransaction }));
      saveSubject.complete();

      // THEN
      expect(financialTransactionFormService.getFinancialTransaction).toHaveBeenCalled();
      expect(financialTransactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinancialTransaction>>();
      const financialTransaction = { id: 123 };
      jest.spyOn(financialTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financialTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(financialTransactionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWallet', () => {
      it('Should forward to walletService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(walletService, 'compareWallet');
        comp.compareWallet(entity, entity2);
        expect(walletService.compareWallet).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

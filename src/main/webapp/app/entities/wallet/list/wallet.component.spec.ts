import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { WalletService } from '../service/wallet.service';

import { WalletComponent } from './wallet.component';

describe('Wallet Management Component', () => {
  let comp: WalletComponent;
  let fixture: ComponentFixture<WalletComponent>;
  let service: WalletService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'wallet', component: WalletComponent }]), HttpClientTestingModule],
      declarations: [WalletComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(WalletComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WalletComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(WalletService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.wallets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to walletService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getWalletIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getWalletIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

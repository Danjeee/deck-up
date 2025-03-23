import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';

@Injectable({
  providedIn: 'root',
})
export class PaymentService extends environmentsURLs{
  private tiendaUrl = `${this.apiURL}/tienda`;

  constructor(private http: HttpClient) {
    super()
  }

  createPayment(amount: string, currency: string, description: string, returnUrl: string, cancelUrl: string) {
    return this.http.post(`${this.tiendaUrl}/pay`, null, {
      params: {
        amount,
        currency,
        description,
        returnUrl,
        cancelUrl
      },
    });
  }
}

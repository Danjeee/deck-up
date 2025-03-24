import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { UserSession } from '../utils/UserSession';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PaymentService extends environmentsURLs{
  private tiendaUrl = `${this.apiURL}/tienda`;

  constructor(private http: HttpClient) {
    super()
  }

  createPayment(amount: string, currency: string, description: string, returnUrl: string, cancelUrl: string, auth:string, cant: number) {
    return this.http.post(`${this.tiendaUrl}/pay`, null, {
      params: {
        amount,
        currency,
        description,
        returnUrl,
        cancelUrl,
        auth,
        cant
      },
    });
  }

  verifyPayments() : Observable<any>{
    const data: FormData = new FormData
    data.append("auth", UserSession.getUser().auth)
    return this.http.post(`${this.tiendaUrl}/verify`, data).pipe(
      catchError(err => {throw err})
    )
  }
}

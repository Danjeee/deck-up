import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';

@Injectable({
  providedIn: 'root'
})
export class TiendaService extends environmentsURLs{

  tiendaURL = this.apiURL + "/tienda"

  constructor(private http: HttpClient) {
    super()
   }

  get() : Observable<any>{
    return this.http.get(`${this.tiendaURL}/get`).pipe(
      catchError(err => {throw err})
    )
  }

}

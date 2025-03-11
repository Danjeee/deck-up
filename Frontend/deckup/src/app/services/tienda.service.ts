import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TiendaService {

  apiURL = "https://localhost:8888/tienda"

  constructor(private http: HttpClient) { }

  get() : Observable<any>{
    return this.http.get(`${this.apiURL}/get`).pipe(
      catchError(err => {throw err})
    )
  }

}

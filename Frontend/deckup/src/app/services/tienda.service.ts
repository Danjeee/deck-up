import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class TiendaService extends environmentsURLs{

  tiendaURL = this.apiURL + "/tienda"
  packURL = this.apiURL + "/packs"

  constructor(private http: HttpClient) {
    super()
   }

  get() : Observable<any>{
    return this.http.get(`${this.tiendaURL}/get`).pipe(
      catchError(err => {throw err})
    )
  }
  getgems() : Observable<any>{
    return this.http.get(`${this.tiendaURL}/getGems`).pipe(
      catchError(err => {throw err})
    )
  }
  buy(id: any) : Observable<any>{
    const data:FormData = new FormData(); 
    data.append("artifact_id", id)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.tiendaURL}/buy`, data)
  }
  buy_pack(id: any) : Observable<any>{
    const data:FormData = new FormData(); 
    data.append("artifact_id", id)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.packURL}/buy`, data)
  }

}

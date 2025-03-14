import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';

@Injectable({
  providedIn: 'root'
})
export class ApacheService extends environmentsURLs{

  imgUrl = this.resURL + "/Resources/php/imagesend.php"

  constructor(private http:HttpClient) {
    super()
   }

  uploadUserImg(body: FormData): Observable<any> {
    return this.http.post(`${this.imgUrl}`,body).pipe(
      catchError(err => {throw err})
    )
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApacheService {

  imgUrl = "http://localhost/Resources/php/imagesend.php"

  constructor(private http:HttpClient) { }

  uploadUserImg(body: FormData): Observable<any> {
    return this.http.post(`${this.imgUrl}`,body).pipe(
      catchError(err => {throw err})
    )
  }
}

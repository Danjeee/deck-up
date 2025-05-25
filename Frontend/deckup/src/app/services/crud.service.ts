import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';

@Injectable({
  providedIn: 'root'
})
export class CrudService extends environmentsURLs {

  module: any

  constructor(private http: HttpClient) {
    super()
  }

  setModule(mod: any){
    this.module = mod;
  }

  getItems(): Observable<any[]> {
    return this.http.get<any[]>(this.apiURL + `/${this.module}/all`);
  }
  saveItem(data: FormData): Observable<any> {
    const formDataObject: any = {};
    data.forEach((value, key) => {
      formDataObject[key] = value;
    });

    // Convertir el objeto a JSON string
    const formDataString = JSON.stringify(formDataObject);

    const final = new FormData();
    final.append("data", formDataString)
    return this.http.post<any>(this.apiURL + `/${this.module}/save`, final);
  }

  deleteItem(id: number): Observable<any> {
    return this.http.delete(`${this.apiURL}/${this.module}/delete/${id}`);
  }
}

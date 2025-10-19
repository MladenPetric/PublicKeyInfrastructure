import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { UserRegistrationRequestDto } from "../dto/registration/register.dto";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  private apiUrl = 'https://localhost:8080/api/auth'; 

  constructor(private http: HttpClient) {}

  registerUser(user: UserRegistrationRequestDto): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user);
  }

}
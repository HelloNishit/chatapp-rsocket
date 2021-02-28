import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "src/app/User";


@Injectable()
export class UserService {
    constructor(private http: HttpClient) { }

    getUsers(): Observable<Object> {
        return this.http.get("http://localhost:8080/getUsers", {
            headers: new HttpHeaders({
                'Content-Type':  'application/json'
            }),
            responseType: 'json',
        });
    }
}
import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class AccountService  {
    constructor(private http: Http) { }

    get(): Observable<any> {
        return this.http.get('api/account')
            .map((res: Response) => res.json())
            .map(data => {
                // get nested account and set at top level
                let account = data.account;
                // change Stormpath groups to fit expected JHipster groups
                let authorities = [];
                if (account.groups) {
                    account.groups.items.forEach(item => {
                        authorities.push('ROLE_' + item.name.toUpperCase());
                    });
                }
                account.authorities = authorities;
                return account;
            });
    }

    save(account: any): Observable<Response> {
        return this.http.post('api/account', account);
    }
}

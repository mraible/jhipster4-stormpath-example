import { Injectable } from '@angular/core';
import { Http, Response, Headers, URLSearchParams, ResponseOptionsArgs } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { LocalStorageService, SessionStorageService } from 'ng2-webstorage';

@Injectable()
export class AuthServerProvider {
    public OAUTH_HEADERS: ResponseOptionsArgs = {
        headers: new Headers({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
        })
    };

    constructor(private http: Http,
                private $localStorage: LocalStorageService,
                private $sessionStorage: SessionStorageService) {
    }

    getToken() {
        return this.$localStorage.retrieve('authenticationToken') || this.$sessionStorage.retrieve('authenticationToken');
    }

    login(credentials): Observable<any> {
        let data: string = 'username=' + encodeURIComponent(credentials.username) + '&password=' +
            encodeURIComponent(credentials.password) + '&grant_type=password';

        return this.http.post('api/oauth/token', data, this.OAUTH_HEADERS).map(authenticateSuccess.bind(this));

        function authenticateSuccess(resp) {
            let jwt = resp.json().access_token;
            this.storeAuthenticationToken(jwt, credentials.rememberMe);
            return jwt;
        }
    }

    loginWithToken(jwt, rememberMe) {
        if (jwt) {
            this.storeAuthenticationToken(jwt, rememberMe);
            return Promise.resolve(jwt);
        } else {
            return Promise.reject('auth-jwt-service Promise reject'); // Put appropriate error message here
        }
    }

    storeAuthenticationToken(jwt, rememberMe) {
        if (rememberMe) {
            this.$localStorage.store('authenticationToken', jwt);
        } else {
            this.$sessionStorage.store('authenticationToken', jwt);
        }
    }

    logout(): Observable<any> {
        let token = this.getToken();
        let tokenHint: any = 'access_token';
        let data: any = 'token=' + encodeURIComponent(token) + '&token_type_hint=' + encodeURIComponent(tokenHint);

        return this.http.post('api/oauth/revoke', data, this.OAUTH_HEADERS).map(() => {
            this.$localStorage.clear('authenticationToken');
            this.$sessionStorage.clear('authenticationToken');
        });
    }
}

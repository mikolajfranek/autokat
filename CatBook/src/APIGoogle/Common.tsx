import { getLocalStorageString, setLocalStorage } from "../LocalStorage";
import { LocalStorageKeys } from "../Enums/LocalStorageKeys";
import { getBearerToken } from "./OAuth2";
import AuthData from './miki-916.json';
//import AuthData from './auto-kat.json';

export function getHeaders(headers: Headers): Headers {
    headers.set('Authorization', `Bearer ${getLocalStorageString(LocalStorageKeys.bearerToken)}`);
    headers.set('tqx', 'out:json');
    return headers;
}

export async function generateToken() {
    var token = await getBearerToken({
        aud: AuthData.token_uri,
        iss: AuthData.client_email,
        scope: 'https://www.googleapis.com/auth/spreadsheets',
        private_key: getPrivateKey()
    });
    setLocalStorage(LocalStorageKeys.bearerToken, token);
}

export function getSpreadsheetIdLogin() {
    return AuthData.spreadsheet_login;
}

export function getSpreadsheetIdCatalyst() {
    return AuthData.spreadsheet_catalyst;
}

export function getPrivateKey() {
    return AuthData.private_key;
}
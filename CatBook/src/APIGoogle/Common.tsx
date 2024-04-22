import { getLocalStorageString, setLocalStorage } from '../LocalStorage';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { getBearerToken } from './OAuth2';
import AuthData from './miki-916.json';
import { APIResponseGetLogin } from './APIDocs';
import { APIResponsePutUid } from './APISheets';
import { APISheetColumnOfTableLogin } from '../Enums/APISheetColumnOfTableLogin';
import { getUniqueId } from 'react-native-device-info';
//import AuthData from './auto-kat.json';

export function getHeaders(headers: Headers): Headers {
    headers.set('Authorization', `Bearer ${getLocalStorageString(LocalStorageKeys.bearerToken)}`);
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

export async function loadUser(login: string, getLogin: any, putUid: any) {
    if (login.length == 0)
        throw Error();
    const user = (await getLogin({ login: login }).unwrap()) as APIResponseGetLogin;
    const user_base = user.table.rows[0];
    const user_id = user_base.c[APISheetColumnOfTableLogin.A_id];
    const user_uid = user_base.c[APISheetColumnOfTableLogin.C_uuid];
    const uid = await getUniqueId();
    if (user_uid != null) {
        if (user_uid.v != uid)
            throw Error();
    } else {
        const result = (await putUid({ id: user_id.v, uid: uid }).unwrap()) as APIResponsePutUid;
        if (result.updatedCells != 1 || result.updatedColumns != 1 || result.updatedRows != 1)
            throw Error();
    }
    setLocalStorage(LocalStorageKeys.loginSheet, JSON.stringify(user_base));
}
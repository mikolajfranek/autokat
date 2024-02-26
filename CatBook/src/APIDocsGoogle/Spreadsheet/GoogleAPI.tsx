import * as Secret from '../Secret';
import { KJUR } from 'jsrsasign';
import UserDevAPI from '../lyrical-ring-412422-5e3581355fd5.json';

export const URL = 'https://docs.google.com/a/google.com/spreadsheets/d/';
export const URL_SUFFIX = '/gviz/tq';

export async function getHeaders() {
    return {
        headers: {
            'Content-type': 'application/json',
            'Authorization': `Bearer ${await getBearerToken()}`,
            'tqx': 'out:json',
            'tq': 'ASSIGN QUERY HERE',
        }
    }
}

export async function getBearerToken() {
    var token = await getToken();
    //debugger;
    return token;
}

async function getToken() {
    var pkcs8 = Secret.getPrivateKey();
    // Header
    var oHeader = { alg: 'RS256', typ: 'JWT' };
    // Payload
    var tNow = KJUR.jws.IntDate.get('now');
    var tEnd = KJUR.jws.IntDate.get('now + 1hour');
    var oPayload = {
        scope: 'https://www.googleapis.com/auth/spreadsheets',
        iss: UserDevAPI.client_email,
        aud: UserDevAPI.token_uri,
        iat: tNow,
        exp: tEnd,
    };
    // Sign JWT
    var sHeader = JSON.stringify(oHeader);
    var sPayload = JSON.stringify(oPayload);
    var sJWT = KJUR.jws.JWS.sign('RS256', sHeader, sPayload, pkcs8);
    //Send
    const bodyJson =
    {
        'grant_type': 'urn:ietf:params:oauth:grant-type:jwt-bearer',
        'assertion': sJWT
    };
    console.log(sJWT);
    const options = {
        method: 'POST',
        body: JSON.stringify(bodyJson),
        eaders: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    };
    return await fetch(UserDevAPI.token_uri, options)
        .then(response => {
            if (response.status != 200)
                throw new Error();
            return response.json();
        })
        .then(responseJSON => {
            return responseJSON.access_token;
        }).catch(error => {
            console.error(error);
        });

}
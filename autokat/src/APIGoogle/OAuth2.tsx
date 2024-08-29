import { KJUR } from 'jsrsasign';

type APIResponse = {
    access_token: string
};

type APIParams = {
    private_key: string,
    scope: string,
    iss: string,
    aud: string
};

export async function getBearerToken(arg: APIParams) {
    // Header
    const oHeader = { alg: 'RS256', typ: 'JWT' };
    // Payload
    const tNow = KJUR.jws.IntDate.get('now');
    const tEnd = KJUR.jws.IntDate.get('now + 1hour');
    const oPayload = {
        scope: arg.scope,
        iss: arg.iss,
        aud: arg.aud,
        iat: tNow,
        exp: tEnd
    };
    // Sign JWT
    const sHeader = JSON.stringify(oHeader);
    const sPayload = JSON.stringify(oPayload);
    const sJWT = KJUR.jws.JWS.sign('RS256', sHeader, sPayload, arg.private_key);
    // Send
    const options = {
        method: 'POST',
        body: JSON.stringify({
            'grant_type': 'urn:ietf:params:oauth:grant-type:jwt-bearer',
            'assertion': sJWT
        }),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    };
    return await fetch(arg.aud, options)
        .then(response => {
            if (response.status != 200)
                throw new Error();
            return response.json() as unknown as APIResponse;
        })
        .then(responseJSON => responseJSON.access_token);
}
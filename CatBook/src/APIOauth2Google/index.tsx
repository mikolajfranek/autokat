import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
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

export const apiOAuth2Google = createApi({
    reducerPath: 'apiOAuth2Google',
    baseQuery: fetchBaseQuery({
        baseUrl: '',
        prepareHeaders: (headers, { }) => {
            headers.set('Accept', 'application/json');
            headers.set('Content-Type', 'application/json');
            return headers;
        }
    }),
    endpoints: builder => ({
        getToken: builder.query<APIResponse, APIParams>({
            query: (arg) => {
                // Header
                var oHeader = { alg: 'RS256', typ: 'JWT' };
                // Payload
                var tNow = KJUR.jws.IntDate.get('now');
                var tEnd = KJUR.jws.IntDate.get('now + 1hour');
                var oPayload = {
                    scope: arg.scope,
                    iss: arg.iss,
                    aud: arg.aud,
                    iat: tNow,
                    exp: tEnd,
                };
                // Sign JWT
                var sHeader = JSON.stringify(oHeader);
                var sPayload = JSON.stringify(oPayload);
                var sJWT = KJUR.jws.JWS.sign('RS256', sHeader, sPayload, arg.private_key);
                return {
                    method: 'POST',
                    url: arg.aud,
                    body: {
                        grant_type: 'urn:ietf:params:oauth:grant-type:jwt-bearer',
                        assertion: sJWT
                    }
                };
            }
        })
    })
});

export const { useGetTokenQuery } = apiOAuth2Google;
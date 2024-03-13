import { BaseQueryFn, FetchArgs, FetchBaseQueryError, createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { APISheetColumnOfTableLogin } from '../../Enums/APISheetColumnOfTableLogin';
import { getLocalStorage, setLocalStorage } from '../../Database/DBA';
import { LocalStorageKeys } from '../../Enums/LocalStorageKeys';
import AuthData from '../miki-916.json';
//import AuthData from '../auto-kat.json';
import { getBearerToken } from '../OAuth2';

type APIResponse = {
    table: {
        rows: {
            c: {
                [key in APISheetColumnOfTableLogin]: {
                    v: string
                }
            }
        }[]
    }
};

type APIParams = {

};

function parseToJSON(input: string): APIResponse {
    return JSON.parse(input.match(/{.*}/gm)![0]);
}

const baseQuery = fetchBaseQuery({
    baseUrl: 'https://docs.google.com/a/google.com/spreadsheets/d',
    prepareHeaders: async (headers, { }) => {
        let token = "";
        try {
            token = await getLocalStorage(LocalStorageKeys.bearerToken);
        } catch (error) {
            //
        }
        headers.set('Authorization', `Bearer ${token}`);
        headers.set('tqx', 'out:json');
        return headers;
    }
});

const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);
    if (result.error && result.error.status === 401) {
        try {
            var token = await getBearerToken({
                aud: AuthData.token_uri,
                iss: AuthData.client_email,
                scope: 'https://www.googleapis.com/auth/spreadsheets',
                private_key: AuthData.private_key
            });
            await setLocalStorage(LocalStorageKeys.bearerToken, token);
            result = await baseQuery(args, api, extraOptions);
        } catch (error) {
            //
        }
    }
    return result
}

export const apiGoogleDocs = createApi({
    reducerPath: 'apiGoogleDocs',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getLogin: builder.query<APIResponse, APIParams>({
            query: (arg) => {
                return {
                    responseHandler: 'text',
                    url: `${AuthData.spreadsheet_login}/gviz/tq`,
                    params: { tq: `select * where B='${"m"}'` }
                }
            },
            transformResponse: (response: string) => parseToJSON(response)
        })
    })
})

export const { useGetLoginQuery } = apiGoogleDocs
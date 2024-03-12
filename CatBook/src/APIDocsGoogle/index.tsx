import { BaseQueryFn, FetchArgs, FetchBaseQueryError, createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { APISheetColumnOfTableLogin } from '../Enums/APISheetColumnOfTableLogin';
import type { RootState } from '../store';
import { getLocalStorage, setLocalStorage } from '../Database/DBA';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { useAppDispatch } from '../hooks';
import { setBearerToken } from '../Slices/Auth';
import { useGetTokenMutation } from '../APIOAuth2Google';
import AuthData from './miki-916.json';
//import AuthData from './auto-kat.json';

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
    prepareHeaders: async (headers, { getState }) => {
        try {
            let token = "";
            console.log(token);
            token = (getState() as RootState).auth.bearerToken;
            console.log(token);
            if (!token) {
                token = await getLocalStorage(LocalStorageKeys.bearerToken);
                const dispatch = useAppDispatch();
                dispatch(setBearerToken(token));
            }
            console.log(token);
            headers.set('Authorization', `Bearer ${token}`);
            headers.set('tqx', 'out:json');
        } catch (error) {
            //
        }
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

            /*
  [Error: Invalid hook call. Hooks can only be called inside of the body of a function component. This could happen for one of the following reasons:
1. You might have mismatching versions of React and the renderer (such as React DOM)
2. You might be breaking the Rules of Hooks
3. You might have more than one copy of React in the same app
See https://react.dev/link/invalid-hook-call for tips about how to debug and fix this problem.]
            */
            const [getToken] = useGetTokenMutation();
            var resultToken = await getToken({
                aud: AuthData.token_uri,
                iss: AuthData.client_email,
                scope: 'https://www.googleapis.com/auth/spreadsheets',
                private_key: AuthData.private_key
            }).unwrap();
            console.log(resultToken.access_token);
            console.log('---------4');
            await setLocalStorage(LocalStorageKeys.bearerToken, resultToken.access_token);
            result = await baseQuery(args, api, extraOptions);
        } catch (error) {
            console.log(error);
            console.log('---------3');
            //
        }
    }
    return result
}

export const apiDocsGoogle = createApi({
    reducerPath: 'apiDocsGoogle',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getLogin: builder.query<APIResponse, APIParams>({
            query: (arg) => {
                return {
                    responseHandler: "text",
                    url: `${AuthData.spreadsheet_login}/gviz/tq`,
                    params: { tq: `select * where B='${"m"}'` }
                }
            },
            transformResponse: (response: string) => parseToJSON(response)
        })
    })
})

export const { useGetLoginQuery } = apiDocsGoogle
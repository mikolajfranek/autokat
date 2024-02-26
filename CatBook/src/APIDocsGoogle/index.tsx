import { BaseQueryFn, FetchArgs, FetchBaseQueryError, createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { getBearerToken } from './Spreadsheet/GoogleAPI';
import { APISheetColumnOfTableLogin } from '../Enums/APISheetColumnOfTableLogin';
import type { RootState } from '../store'
import { getLocalStorage } from '../Database/DBA';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';

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

type AuthParams = {
    mail: string,
    company?: string,
    password?: string,
    serialID: string,
};

type APIParamsTableLogin = {
    spreadsheetId: string,
    authParams: AuthParams
};

function parseToJSON(input: string): APIResponse {
    return JSON.parse(input.match(/{.*}/gm)![0]);
}

const baseQuery = fetchBaseQuery({
    baseUrl: 'https://docs.google.com/a/google.com/spreadsheets/d',
    prepareHeaders: async (headers, { getState }) => {
        let token = "";
        token = (getState() as RootState).user.bearerToken;
        //try catch?
        if (!token)
            token = await getLocalStorage(LocalStorageKeys.bearerToken);
        if (!token)
            token = await getBearerToken();
        headers.set('Authorization', `Bearer ${token}`);
        headers.set('tqx', 'out:json');
        return headers;
    },
});

const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions)
    if (result.error && result.error.status === 401) {


        // try to get a new token
        const refreshResult = await baseQuery('/refreshToken', api, extraOptions)
        if (refreshResult.data) {
            // store the new token
            //api.dispatch(tokenReceived(refreshResult.data))
            // retry the initial query
            result = await baseQuery(args, api, extraOptions)
        } else {
            //api.dispatch(loggedOut())
        }
    }
    return result
}

export const apiSheet = createApi({
    reducerPath: 'apiDocsGoogle',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getLogin: builder.query<APIResponse, APIParamsTableLogin>({
            query: (arg) => {
                return {
                    responseHandler: "text",
                    url: `${arg.spreadsheetId}/gviz/tq`,
                    params: { tq: `select * where A='${arg.authParams.mail}'` }
                }
            },
            transformResponse: (response: string) => parseToJSON(response)
        })
    })
})

export const { useGetLoginQuery } = apiSheet
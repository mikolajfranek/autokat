import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { getBearerToken } from './Spreadsheet/GoogleAPI';
import { APISheetColumnOfTableLogin } from '../Enums/APISheetColumnOfTableLogin';

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

export const apiSheet = createApi({
    reducerPath: 'apiSheet',
    baseQuery: fetchBaseQuery({
        baseUrl: 'https://docs.google.com/a/google.com/spreadsheets/d',
        prepareHeaders: async (headers, { getState }) => {
            const token = await getBearerToken();
            //TODO?
            //(getState() as RootState).auth.token;
            headers.set('Authorization', `Bearer ${token}`);
            headers.set('tqx', 'out:json');
            return headers;
        },
    }),

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
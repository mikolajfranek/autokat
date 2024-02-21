import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { getBearerToken } from './Spreadsheet/GoogleAPI';

type APIResponse = {
    data: string
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
                    url: `${arg.spreadsheetId}/gviz/tq`,
                    headers: { tq: `select * where A='${arg.authParams.mail}'` }
                }
            }, 

            transformResponse: (response: string, meta, arg) => {
                var data = JSON.parse(response.match(/{.*}/gm)[0]);
                console.log('----');
                console.log(data);
                return data;
            }
        })
    })
})

export const { useGetLoginQuery } = apiSheet
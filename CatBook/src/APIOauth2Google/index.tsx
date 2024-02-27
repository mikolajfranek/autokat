import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

type APIResponse = {

};

type APIParams = {

};

export const apiOAuth2Google = createApi({
    reducerPath: 'apiOAuth2Google',




    baseQuery: fetchBaseQuery({
        baseUrl: '',
        prepareHeaders: async (headers, { }) => {
            let token = "";

            headers.set('Authorization', `Bearer ${token}`);
            headers.set('tqx', 'out:json');
            return headers;
        },
    }),
    endpoints: builder => ({
        getToken: builder.query<APIResponse, APIParams>({
            query: (arg) => {



                return {
                    method: 'POST',
                    responseHandler: "text",
                    //url: `${arg.spreadsheetId}/gviz/tq`,
                    //params: { tq: `select * where A='${arg.authParams.mail}'` }
                }
            }
        })
    })
})

export const { useGetTokenQuery } = apiOAuth2Google
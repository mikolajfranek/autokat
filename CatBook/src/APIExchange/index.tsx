import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export interface Rates {
    mid: string,
    effectiveDate: string
}
type APIResponse = {
    rates: Rates[]
};

export const apiExchange = createApi({
    reducerPath: 'apiExchange',
    baseQuery: fetchBaseQuery({ baseUrl: 'https://api.nbp.pl' }),
    endpoints: builder => ({
        getUSD: builder.query<APIResponse, void>({
            query: () => {
                //
                return {
                    url: '/api/exchangerates/rates/a/usd?format=json'
                };
            }
        }),
        getEUR: builder.query<APIResponse, void>({
            query: () => {
                //
                return {
                    url: '/api/exchangerates/rates/a/eur?format=json'
                };
            }
        })
    })
})

export const { useGetUSDQuery, useGetEURQuery } = apiExchange
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
        //TODO jak uzyskac domyslna wartosc parametru?
        getUSD: builder.query<APIResponse, { dataKursu: Date }>({
            query: (arg) => {
                const { dataKursu } = arg;
                const dataString = dataKursu.toLocaleDateString('en-CA');
                return {
                    url: `/api/exchangerates/rates/a/usd/${dataString}?format=json`
                };
            }
        }),
        getEUR: builder.query<APIResponse, void>({
            query: (arg) => {
                //
                return {
                    url: '/api/exchangerates/rates/a/eur?format=json'
                };
            }
        })
    })
})

export const { useGetUSDQuery, useGetEURQuery } = apiExchange
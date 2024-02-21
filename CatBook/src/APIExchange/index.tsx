import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export interface Rates {
    mid: string,
    effectiveDate: string
}
type APIResponse = {
    rates: Rates[]
};

type APIParams = {
    dataKursu: string
};

export const apiExchange = createApi({
    reducerPath: 'apiExchange',
    baseQuery: fetchBaseQuery({ baseUrl: 'https://api.nbp.pl' }),
    endpoints: builder => ({
        getUSD: builder.query<APIResponse, APIParams | void>({
            query: (arg: APIParams = { dataKursu: new Date().toLocaleDateString('sv-SE') }) => {
                const { dataKursu } = arg;
                return {
                    url: `/api/exchangerates/rates/a/usd/${dataKursu}?format=json`
                };
            }
        }),
        getEUR: builder.query<APIResponse, APIParams | void>({
            query: (arg: APIParams = { dataKursu: new Date().toLocaleDateString('sv-SE') }) => {
                const { dataKursu } = arg;
                return {
                    url: `/api/exchangerates/rates/a/eur/${dataKursu}?format=json`
                };
            }
        })
    })
})

export const { useGetUSDQuery, useGetEURQuery } = apiExchange
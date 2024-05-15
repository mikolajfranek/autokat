import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { Metal } from '../Enums/Metal';

type Results = {
    results: {
        bid: number
    }[]
}

type APIResponse = {
    data: {
        [key in keyof typeof Metal]: Results
    }
}

type APIParams = {
    data_kursu: Date
};

export const apiMetal = createApi({
    reducerPath: 'apiMetal',
    baseQuery: fetchBaseQuery({ baseUrl: 'https://kitco-gcdn-prod.stellate.sh' }),
    endpoints: builder => ({
        getMetals: builder.mutation<APIResponse, APIParams>({
            query: (arg) => {
                const timestamp = Math.floor(arg.data_kursu.getTime() / 1000);
                return {
                    method: 'POST',
                    url: '',
                    body: {
                        'query': 'fragment MetalFragment on Metal { symbol currency results { ...MetalQuoteFragment } } fragment MetalQuoteFragment on Quote { bid unit } query AllMetalsQuote($currency: String!, $timestamp: Int) { platinum: GetMetalQuote( symbol: \"PT\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } palladium: GetMetalQuote( symbol: \"PD\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } rhodium: GetMetalQuote( symbol: \"RH\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } }',
                        'variables': {
                            'currency': 'USD',
                            'timestamp': timestamp
                        }
                    }
                };
            }
        })
    })
});

export const { useGetMetalsMutation } = apiMetal;
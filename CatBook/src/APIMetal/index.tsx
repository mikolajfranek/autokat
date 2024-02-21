import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { Metal } from '../Enums/Metal';

type Results = {
    results: {
        bid: string
    }[]
}

type APIResponse = {
    data: {
        [key in keyof typeof Metal]: Results
    }
};

export const apiMetal = createApi({
    reducerPath: 'apiMetal',
    baseQuery: fetchBaseQuery({ baseUrl: 'https://kitco-gcdn-prod.stellate.sh' }),
    endpoints: builder => ({
        getCourses: builder.query<APIResponse, void>({
            query: () => {
                const timestamp = Math.floor(Date.now() / 1000);
                return {
                    method: 'POST',
                    url: '',
                    body: {
                        'query': 'fragment MetalFragment on Metal { symbol currency results { ...MetalQuoteFragment } } fragment MetalQuoteFragment on Quote { bid unit } query AllMetalsQuote($currency: String!, $timestamp: Int) { platinum: GetMetalQuote( symbol: \"PT\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } palladium: GetMetalQuote( symbol: \"PD\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } rhodium: GetMetalQuote( symbol: \"RH\" timestamp: $timestamp currency: $currency ) { ...MetalFragment } }',
                        'variables': {
                            'currency': 'USD',
                            'timestamp': timestamp
                        }
                    },
                };
            }
        })
    })
})

export const { useGetCoursesQuery } = apiMetal
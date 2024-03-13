import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

type Rates = {
    mid: string,
    effectiveDate: string
};

type APIResponse = {
    rates: Rates[]
};

type APIParams = {
    currency: 'usd' | 'eur',
    dataKursu?: Date
};

export const apiExchange = createApi({
    reducerPath: 'apiExchange',
    baseQuery: fetchBaseQuery({ baseUrl: 'https://api.nbp.pl' }),
    endpoints: builder => ({
        getExchange: builder.mutation<APIResponse, APIParams>({
            query: (arg) => {
                let { currency, dataKursu } = arg;
                if (dataKursu == undefined)
                    dataKursu = new Date();
                return {
                    method: 'GET',
                    url: `/api/exchangerates/rates/a/${currency}/${dataKursu.toLocaleDateString('sv-SE')}?format=json`
                };
            }
        })
    })
});

export const { useGetExchangeMutation } = apiExchange;
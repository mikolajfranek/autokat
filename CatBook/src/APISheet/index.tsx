import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const apiSheet = createApi({
    reducerPath: 'apiSheet',
    baseQuery: fetchBaseQuery({
        baseUrl: '/fakeApi',
        prepareHeaders: (headers, { getState }) => {
            const token = null; //(getState() as RootState).auth.token;
            // If we have a token set in state, let's assume that we should be passing it.
            if (token) {
                headers.set('authorization', `Bearer ${token}`);
            }
            return headers;
        },
    }),
    endpoints: builder => ({
        getPosts: builder.query({
            query: () => '/posts'
        })
    })
})

export const { useGetPostsQuery } = apiSheet
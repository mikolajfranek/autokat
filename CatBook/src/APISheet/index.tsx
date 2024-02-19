import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const apiSheet = createApi({
    reducerPath: 'apiSheet',
    baseQuery: fetchBaseQuery({ baseUrl: '/fakeApi' }),
    endpoints: builder => ({
        getPosts: builder.query({
            query: () => '/posts'
        })
    })
})

export const { useGetPostsQuery } = apiSheet
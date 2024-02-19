import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const apiMetal = createApi({
    reducerPath: 'apiMetal',
    baseQuery: fetchBaseQuery({ baseUrl: '/fakeApi' }),
    endpoints: builder => ({
        getPosts: builder.query({
            query: () => '/posts'
        })
    })
})

export const { useGetPostsQuery } = apiMetal
import { createSlice } from "@reduxjs/toolkit"

export interface UserState {
    islogged: boolean;
}

const initialState: UserState = {
    islogged: false
}

export const slice = createSlice({
    name: "user",
    initialState,
    reducers: {
        loging: (state) => {
            state.islogged = !state.islogged;
        },
    },
});

export const { loging } = slice.actions;

export default slice.reducer
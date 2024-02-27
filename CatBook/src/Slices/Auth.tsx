import { createSlice } from "@reduxjs/toolkit"

export interface AuthState {
    bearerToken: string;
}

const initialState: AuthState = {
    bearerToken: ""
}

export const slice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        //
    },
});

export const { } = slice.actions;

export default slice.reducer
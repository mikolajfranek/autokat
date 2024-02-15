import { createAsyncThunk, createSlice } from "@reduxjs/toolkit"
import { database } from "../Database/DBA";

export interface UserState {
    islogged: boolean;
}

const initialState: UserState = {
    islogged: false
}

export const loginAsync = createAsyncThunk<boolean>(
    "user/loginAsync",
    async () => {
        await database.localStorage.remove("license");
        return true;
    }
);

export const slice = createSlice({
    name: "user",
    initialState,
    reducers: {
        loging: (state) => {
            state.islogged = !state.islogged;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginAsync.fulfilled, (state, action) => {
                if (action.payload)
                    state.islogged = !state.islogged;
            });
    }
});

export const { loging } = slice.actions;

export default slice.reducer
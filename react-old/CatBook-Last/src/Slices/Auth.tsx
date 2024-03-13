import { PayloadAction, createSlice } from "@reduxjs/toolkit"

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
        setBearerToken: (state, action: PayloadAction<string>) => {
            state.bearerToken = action.payload;
        }
    },
});

export const { setBearerToken } = slice.actions;

export default slice.reducer
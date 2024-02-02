import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface UserState {
    mail: string,
    license: boolean;
}

const initialState: UserState = {
    mail: '',
    license: false,
}


export const slice = createSlice({
    name: 'user',
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: {
        loginAction: (state) => {
            state.license = true;
        }
    }
});

export const { loginAction } = slice.actions;


export default slice.reducer

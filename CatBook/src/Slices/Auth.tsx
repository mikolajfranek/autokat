import { PayloadAction, createSlice } from '@reduxjs/toolkit'
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { getLocalStorageBoolean, setLocalStorage } from '../LocalStorage';

export interface AuthState {
    isAuthenticated: boolean;
};

const initialState: AuthState = {
    isAuthenticated: getLocalStorageBoolean(LocalStorageKeys.isAuthenticated)
};

export const slice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setAuthenticated: (state, action: PayloadAction<boolean>) => {
            let value = action.payload;
            setLocalStorage(LocalStorageKeys.isAuthenticated, value);
            state.isAuthenticated = value;
        }
    },
});

export const {
    setAuthenticated
} = slice.actions;

export default slice.reducer;
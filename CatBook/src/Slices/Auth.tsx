import { PayloadAction, createSlice } from '@reduxjs/toolkit'
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';
import { getLocalStorageBoolean, setLocalStorage } from '../LocalStorage';

export interface AuthState {
    status: boolean;
}

const initialState: AuthState = {
    status: getLocalStorageBoolean(LocalStorageKeys.authStatus)
};

export const slice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setAuthStatus: (state, action: PayloadAction<boolean>) => {
            let value = action.payload;
            setLocalStorage(LocalStorageKeys.authStatus, value);
            state.status = value;
        }
    },
});

export const {
    setAuthStatus
} = slice.actions;

export default slice.reducer;
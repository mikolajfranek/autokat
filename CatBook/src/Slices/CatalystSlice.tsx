import { createSlice, PayloadAction } from '@reduxjs/toolkit'

export interface CatalystState {
    needSync: boolean,
}

const initialState: CatalystState = {
    needSync: false,
}

export const slice = createSlice({
    name: 'catalyst',
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: {
        checkSync: (state) => {
            //TODO async
        },
        sync: (state) => {
            //TODO async
        },
        getSpreadsheet: (state) => {
            //TODO async
        },
        getDatabase: (state) => {
            //TODO async
        },
        getPicture: (state) => {
            //TODO async
        }


    }
});

export const { sync } = slice.actions;


export default slice.reducer

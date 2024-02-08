import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { database } from '../Database/DBA';

export interface UserState {
    license: boolean;
    status: 'idle' | 'loading' | 'failed';
}

const initialState: UserState = {
    status: 'idle',
    license: false,
}

//The most common reason to use middleware is to allow different kinds of async logic to interact with the store. 


//Any asynchronicity has to happen outside the store.
export const slice = createSlice({
    name: 'user',
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    //reducers - opisuje jak zmienia sie stan aplikacji w odpowiedzi na akcje
    //wynik reduktora zalezy TYLKO od parametrow wejsciowych
    //nie wykorzystuj math.random w reduktorze - reduktor to przewidywalnossc stanow
    //action - to co się dzieje z aplikacją
    reducers: {


        logout: (state) => {
            state.license = false;
        },

        //zwraca element dziedziczacy z JSX.ELEMENT;
        kreator: (state) => {
            //return new Element(nazwa);
        },

    },
    extraReducers: (builder) => {
        // Add reducers for additional action types here, and handle loading state as needed
        builder
            .addCase(loginUser.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.status = 'idle';
                state.license = action.payload;
            })
            .addCase(loginUser.rejected, (state) => {
                state.status = 'failed';
            });

        // Add reducers for additional action types here, and handle loading state as needed
        builder
            .addCase(logoutUser.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(logoutUser.fulfilled, (state, action) => {
                state.status = 'idle';
                state.license = false;
            })
            .addCase(logoutUser.rejected, (state) => {
                state.status = 'failed';
            });
    },
});



// payload creator callback that does the actual async logic and returns a promise with the result. 
function apiLoginUser() {
    return new Promise<{ data: boolean }>((resolve) =>
        setTimeout(() => resolve({ data: true }), 500)
    );
}

export const loginUser = createAsyncThunk(
    'user/loginUserBlaBla',
    async () => {
        const response = await apiLoginUser();
        if (response.data)
            await database.localStorage.set("license", new Date().getTime());
        // The value we return becomes the `fulfilled` action payload
        //throw Error();
        return response.data;
    }
);

export const logoutUser = createAsyncThunk(
    'user/logoutnUserBlaBla',
    async () => {
        await database.localStorage.remove("license");
        
    }
);





export const { logout } = slice.actions;


export default slice.reducer

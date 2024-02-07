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
    //reducers - opisuje jak zmienia sie stan aplikacji w odpowiedzi na akcje
        //wynik reduktora zalezy TYLKO od parametrow wejsciowych
        //nie wykorzystuj math.random w reduktorze - reduktor to przewidywalnossc stanow
    //action - to co się dzieje z aplikacją
    reducers: {
        
        reducerLoginUser: (state) => {
            state.license = true;
        },


        actionLoginUser: (state) => {
            //TODO async
        },
        refreshUser: (state) => {
            //TODO async
        },
        logout: (state) => {
            //TODO
        },

        //zwraca element dziedziczacy z JSX.ELEMENT;
        kreator: (state) => {
            //return new Element(nazwa);
        },

    }
});

export const { actionLoginUser, reducerLoginUser } = slice.actions;


export default slice.reducer

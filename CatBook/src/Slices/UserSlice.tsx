import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit"
import { database } from "../Database/DBA";
import * as API from "../Spreadsheet/Login";
import { UserStates } from "../Enums/UserStates";
import { OperationStates } from "../Enums/OperationStates";

export interface UserState {
    state: UserStates;
    operationState: OperationStates;
}

const initialState: UserState = {
    state: UserStates.NOT_LOGGED,
    operationState: OperationStates.IDLE,
}

export const autoLogin = createAsyncThunk(
    "user/autoLogin",
    async () => {
        var license = await database.localStorage.get("license");
        console.log(license);
        if (license == undefined)
            return false;
        return true;
    }
);

export const login = createAsyncThunk<boolean, {
    loginInput: string,
    companyInput: string,
    passInput: string,
    processInput: boolean
}>(
    "user/login",
    async (data) => {
        let { loginInput, companyInput, passInput, processInput } = data;
        var license = await API.getUser(loginInput, companyInput, passInput);
        if (license.data == false)
            return false;
        if(processInput)
            await database.localStorage.set("license", "TODO");
        return true;
    }
);

export const logout = createAsyncThunk(
    "user/logout",
    async () => {
        await database.localStorage.remove("license");
        return false;
    }
);

export const slice = createSlice({
    name: "user",
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: {
        //TODO - add reducers
        reducerName: (state) => {
            //TODO - return React.JSX.Element?
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(autoLogin.pending, (state) => {
                state.operationState = OperationStates.LOADING;
            })
            .addCase(autoLogin.fulfilled, (state, action) => {
                state.operationState = OperationStates.IDLE;
                if (action.payload) {
                    state.state = UserStates.LOGGED;
                }
            })
            .addCase(autoLogin.rejected, (state) => {
                state.operationState = OperationStates.FAILED;
            });

        builder
            .addCase(login.pending, (state) => {
                state.operationState = OperationStates.LOADING;
            })
            .addCase(login.fulfilled, (state, action) => {
                state.operationState = OperationStates.IDLE;
                if (action.payload == false) {
                    state.operationState = OperationStates.FAILED;
                } else if (state.state == UserStates.NOT_LOGGED) {
                    state.state = UserStates.NOT_LOGGED_NEED_COMPANY;
                } else if (state.state == UserStates.NOT_LOGGED_NEED_COMPANY) {
                    state.state = UserStates.NOT_LOGGED_NEED_PASS;
                } else {
                    state.state = UserStates.LOGGED;
                }
            })
            .addCase(login.rejected, (state) => {
                state.operationState = OperationStates.FAILED;
            });

        builder
            .addCase(logout.fulfilled, (state, action) => {
                state.state = action.payload ? UserStates.NOT_LOGGED : UserStates.NOT_LOGGED;
            });
    },
});

export const { } = slice.actions;

export default slice.reducer
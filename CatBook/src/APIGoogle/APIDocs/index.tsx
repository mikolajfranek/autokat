import { BaseQueryFn, FetchArgs, FetchBaseQueryError, createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { APISheetColumnOfTableLogin } from '../../Enums/APISheetColumnOfTableLogin';
import { generateToken, getHeaders, getSpreadsheetIdCatalyst, getSpreadsheetIdLogin } from '../Common';
import { APISheetColumnOfTableCatalyst } from '../../Enums/APISheetColumnOfTableCatalyst';

//Login
export type LoginSheet = {
    c: {
        [key in APISheetColumnOfTableLogin]: {
            v: string
        }
    }
};

export type APIResponseGetLogin = {
    table: {
        rows: LoginSheet[]
    }
};

type APIParamsGetLogin = {
    login: string
};

//Catalyst
export type CatalystSheet = {
    c: {
        [key in APISheetColumnOfTableCatalyst]: {
            v: string
        }
    }
};

export type APIResponseGetCatalyst = {
    table: {
        rows: CatalystSheet[]
    }
};

type APIParamsGetCatalyst = {
    fromId: number
};

function parseToJSON(input: string) {
    return JSON.parse(input.match(/{.*}/gm)![0]);
}

const baseQuery = fetchBaseQuery({
    baseUrl: 'https://docs.google.com/a/google.com/spreadsheets/d',
    prepareHeaders: async (headers, { }) => {
        return getHeaders(headers);
    }
});

const baseQueryWithReauth: BaseQueryFn<
    string | FetchArgs,
    unknown,
    FetchBaseQueryError
> = async (args, api, extraOptions) => {
    let result = await baseQuery(args, api, extraOptions);
    if (result.error && result.error.status === 401) {
        try {
            await generateToken();
            result = await baseQuery(args, api, extraOptions);
        } catch (error) {
            //
        }
    }
    return result;
};

export const apiGoogleDocs = createApi({
    reducerPath: 'apiGoogleDocs',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        getLogin: builder.mutation<APIResponseGetLogin, APIParamsGetLogin>({
            query: (arg) => {
                return {
                    responseHandler: 'text',
                    url: `${getSpreadsheetIdLogin()}/gviz/tq`,
                    method: 'GET',
                    params: {
                        //TODO
                        tq:
                            `select * where ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.B_login].toString().substring(0, 1)}='${arg.login}' AND  
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.A_id].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.B_login].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.D_licence].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.E_discount].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.F_visibility].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.G_minusPlatinum].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.H_minusPalladium].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.I_minusRhodium].toString().substring(0, 1)} IS NOT NULL`
                    }
                };
            },
            transformResponse: (response: string) => parseToJSON(response) as APIResponseGetLogin
        }),
        getCatalyst: builder.mutation<APIResponseGetCatalyst, APIParamsGetCatalyst>({
            query: (arg) => {
                return {
                    responseHandler: 'text',
                    url: `${getSpreadsheetIdCatalyst()}/gviz/tq`,
                    method: 'GET',
                    params: {
                        //TODO
                        tq: ''
                            /*
                            `select * where ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.B_login].toString().substring(0, 1)}='${arg.login}' AND  
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.A_id].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.B_login].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.D_licence].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.E_discount].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.F_visibility].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.G_minusPlatinum].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.H_minusPalladium].toString().substring(0, 1)} IS NOT NULL AND 
                            ${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.I_minusRhodium].toString().substring(0, 1)} IS NOT NULL`
                            */
                    }
                };
            },
            transformResponse: (response: string) => parseToJSON(response) as APIResponseGetCatalyst
        })
    })
});

export const { useGetLoginMutation } = apiGoogleDocs;
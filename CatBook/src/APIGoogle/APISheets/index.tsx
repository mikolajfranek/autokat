import { BaseQueryFn, FetchArgs, FetchBaseQueryError, createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { APISheetColumnOfTableLogin } from '../../Enums/APISheetColumnOfTableLogin';
import { generateToken, getHeaders, getSpreadsheetIdLogin } from '../Common';

export type APIResponsePutUid = {
    updatedRows: number,
    updatedColumns: number,
    updatedCells: number
};

type APIParamsPutUid = {
    id: string,
    uid: string
};

const baseQuery = fetchBaseQuery({
    baseUrl: 'https://sheets.googleapis.com/v4/spreadsheets',
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

export const apiGoogleSheets = createApi({
    reducerPath: 'apiGoogleSheets',
    baseQuery: baseQueryWithReauth,
    endpoints: builder => ({
        putUid: builder.mutation<APIResponsePutUid, APIParamsPutUid>({
            query: (arg) => {
                const sheetCell = `użytkownicy!${APISheetColumnOfTableLogin[APISheetColumnOfTableLogin.C_uuid].toString().substring(0, 1)}${(Number.parseInt(arg.id) + 1)}`;
                return {
                    url: `${getSpreadsheetIdLogin()}/values/${sheetCell}`,
                    method: 'PUT',
                    body: {
                        range: sheetCell,
                        majorDimension: 'ROWS',
                        values: [[arg.uid]]
                    },
                    params: {
                        valueInputOption: 'USER_ENTERED'
                    }
                };
            }
        })
    })
});

export const { usePutUidMutation } = apiGoogleSheets;
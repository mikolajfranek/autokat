import { configureStore } from '@reduxjs/toolkit';
import { apiGoogleDocs } from './APIGoogle/APIDocs';
import { apiGoogleSheets } from './APIGoogle/APISheets';
import { apiExchange } from './APIExchange';
import { apiMetal } from './APIMetal';
import Auth from './Slices/Auth';

export const store = configureStore({
  reducer: {
    auth: Auth,
    [apiGoogleDocs.reducerPath]: apiGoogleDocs.reducer,
    [apiGoogleSheets.reducerPath]: apiGoogleSheets.reducer,
    [apiExchange.reducerPath]: apiExchange.reducer,
    [apiMetal.reducerPath]: apiMetal.reducer
  },
  devTools: process.env.NODE_ENV !== 'production',
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware()
      //We need to keep all of the existing standard middleware like redux-thunk 
      //in the store setup, and the API slice's middleware typically goes after those. 
      .concat(apiGoogleDocs.middleware)
      .concat(apiGoogleSheets.middleware)
      .concat(apiExchange.middleware)
      .concat(apiMetal.middleware)
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
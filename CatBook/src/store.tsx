import { configureStore } from '@reduxjs/toolkit';
import User from './Slices/User';
import { apiGoogleDocs } from './APIGoogle/APIDocs';
import { apiExchange } from './APIExchange';
import { apiMetal } from './APIMetal';
import Auth from './Slices/Auth';

export const store = configureStore({
  reducer: {
    user: User,
    auth: Auth,
    [apiGoogleDocs.reducerPath]: apiGoogleDocs.reducer,
    [apiExchange.reducerPath]: apiExchange.reducer,
    [apiMetal.reducerPath]: apiMetal.reducer,
  },
  devTools: process.env.NODE_ENV !== 'production',

  //We need to keep all of the existing standard middleware like redux-thunk 
  //in the store setup, and the API slice's middleware typically goes after those. 
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware()
      .concat(apiGoogleDocs.middleware)
      .concat(apiExchange.middleware)
      .concat(apiMetal.middleware)
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
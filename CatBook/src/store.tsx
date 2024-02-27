import { configureStore } from '@reduxjs/toolkit';
import User from './Slices/User';
import { apiDocsGoogle } from './APIDocsGoogle';
import { apiExchange } from './APIExchange';
import { apiMetal } from './APIMetal';
import { apiOAuth2Google } from './APIOAuth2Google';
import Auth from './Slices/Auth';

export const store = configureStore({
  reducer: {
    user: User,
    auth: Auth,
    [apiDocsGoogle.reducerPath]: apiDocsGoogle.reducer,
    [apiExchange.reducerPath]: apiExchange.reducer,
    [apiMetal.reducerPath]: apiMetal.reducer,
    [apiOAuth2Google.reducerPath]: apiOAuth2Google.reducer,
  },
  devTools: process.env.NODE_ENV !== 'production',

  //We need to keep all of the existing standard middleware like redux-thunk 
  //in the store setup, and the API slice's middleware typically goes after those. 
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware()
      .concat(apiDocsGoogle.middleware)
      .concat(apiExchange.middleware)
      .concat(apiMetal.middleware)
      .concat(apiOAuth2Google.middleware)
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
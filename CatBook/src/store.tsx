import { configureStore } from '@reduxjs/toolkit';
import User from './Slices/User';
import { apiSheet } from './APIDocsGoogle';
import { apiMetal } from './APIMetal';
import { apiExchange } from './APIExchange';

export const store = configureStore({
  reducer: {
    user: User,
    [apiSheet.reducerPath]: apiSheet.reducer,
    [apiMetal.reducerPath]: apiMetal.reducer,
    [apiExchange.reducerPath]: apiExchange.reducer
  },
  devTools: process.env.NODE_ENV !== 'production',

  //We need to keep all of the existing standard middleware like redux-thunk 
  //in the store setup, and the API slice's middleware typically goes after those. 
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware()
      .concat(apiSheet.middleware)
      .concat(apiMetal.middleware)
      .concat(apiExchange.middleware)
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
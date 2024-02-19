import { configureStore } from '@reduxjs/toolkit';
import User from './Slices/User';
import { apiSheet } from './APISheet';
import { apiMetal } from './APIMetal';

export const store = configureStore({
  reducer: {
    user: User,
    [apiSheet.reducerPath]: apiSheet.reducer,
    [apiMetal.reducerPath]: apiMetal.reducer
  },
  devTools: process.env.NODE_ENV !== 'production',

  //We need to keep all of the existing standard middleware like redux-thunk 
  //in the store setup, and the API slice's middleware typically goes after those. 
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware()
      .concat(apiSheet.middleware)
      .concat(apiMetal.middleware)
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
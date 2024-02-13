import { configureStore, ThunkAction, Action } from '@reduxjs/toolkit';
import UserSlice from './Slices/UserSlice';
//import CatalystSlice from './Slices/CatalystSlice';
import logger from 'redux-logger';

export const store = configureStore({
  reducer: {
    user: UserSlice,
    //catalyst: CatalystSlice
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(logger),
  devTools: process.env.NODE_ENV !== 'production',
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
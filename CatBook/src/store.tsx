import { configureStore } from '@reduxjs/toolkit';
import User from './Slices/User';

export const store = configureStore({
  reducer: {
    user: User,
  },
  devTools: process.env.NODE_ENV !== 'production',
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
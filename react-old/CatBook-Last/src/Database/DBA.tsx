import { Database } from '@nozbe/watermelondb';
import SQLiteAdapter from '@nozbe/watermelondb/adapters/sqlite';
import schema from './Schema';
import migrations from './Migrations';
import Course from './Models/Course';
import Filter from './Models/Filter';
import Catalyst from './Models/Catalyst';
import { LocalStorageKeys } from '../Enums/LocalStorageKeys';

const adapter = new SQLiteAdapter({
  schema,
  migrations,
  jsi: true,
  onSetUpError: error => {
    //TODO
    // Database failed to load -- offer the user to reload the app or log out
  }
})

export async function getLocalStorage(key: LocalStorageKeys): Promise<string> {
  return await database.localStorage.get(key.toString()) as string;
}

export async function setLocalStorage(key: LocalStorageKeys, value: string): Promise<void> {
  return await database.localStorage.set(key.toString(), value);
}

export const database = new Database({
  adapter,
  modelClasses: [
    Catalyst, Course, Filter
  ],
})
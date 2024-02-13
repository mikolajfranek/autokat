import { Database } from '@nozbe/watermelondb';
import SQLiteAdapter from '@nozbe/watermelondb/adapters/sqlite';
import schema from './Schema';
import migrations from './Migrations';
import Course from './Models/Course';
import Filter from './Models/Filter';
import Catalyst from './Models/Catalyst';

const adapter = new SQLiteAdapter({
  schema,
  migrations,
  jsi: true,
  onSetUpError: error => {
    // Database failed to load -- offer the user to reload the app or log out
  }
})

export const database = new Database({
  adapter,
  modelClasses: [
    Catalyst, Course, Filter
  ],
})
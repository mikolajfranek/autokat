import { Database } from '@nozbe/watermelondb';
import SQLiteAdapter from '@nozbe/watermelondb/adapters/sqlite';
import schema from './Schema';
import migrations from './Migrations';
import Filter from './Models/Filter';
import Catalyst from './Models/Catalyst';
import { Alert } from 'react-native';
import CourseMetal from './Models/CourseMetal';
import CourseExchange from './Models/CourseExchange';

const adapter = new SQLiteAdapter({
  schema,
  migrations,
  jsi: true,
  onSetUpError: error => {
    Alert.alert(
      'Błąd ładowania bazy danych',
      'W celu rozwiązania problemu proszę o ponowne uruchomienie bądź zainstalowanie aplikacji - jeśli to nie pomoże proszę skontaktować się z deweloperem aplikacji.');
    Alert.alert(
      error.name,
      `${error.message}\n${error.stack}`);
  }
});

export const database = new Database({
  adapter,
  modelClasses: [
    Catalyst, CourseMetal, CourseExchange, Filter
  ]
});
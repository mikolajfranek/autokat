import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { useAppSelector } from './hooks';
import MaterialBottomTab from './GUI/MaterialBottomTab';
import FormularzLogowania from './GUI/FormularzLogowania';
import ModalKursyWalutWybierzDate from './GUI/Modale/ModalKursyWalutWybierzDate';

const Stack = createStackNavigator();

export default function App(): React.JSX.Element {
  const isAuthenticated = useAppSelector((state) => state.auth.isAuthenticated);
  return (
    <Stack.Navigator id='Stack_Aplikacja'>
      {
        isAuthenticated
          ?
          <Stack.Screen
            name='Stack.Screen_MaterialBottomTab'
            component={MaterialBottomTab}
            options={{ header: () => undefined }} />
          :
          (
            <Stack.Screen
              name='Stack.Screen_FormularzLogowania'
              component={FormularzLogowania}
              options={{ header: () => undefined }}
            />
          )
      }
      <Stack.Group screenOptions={{ presentation: 'modal' }}>
        <Stack.Screen
          name='Stack.Screen_ModalKursyWalutWybierzDate'
          component={ModalKursyWalutWybierzDate}
          options={{ title: 'Wybierz datę' }} />
      </Stack.Group>
    </Stack.Navigator>
  );
}
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import React from 'react';
import FormLogin from './Features/FormLogin';
import BaseModal from './Features/Modals/BaseModal';

const Stack = createNativeStackNavigator();

export default function App(): React.JSX.Element {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="FormLogin"
        screenOptions={{
          headerStyle: { backgroundColor: 'lightblue' },
        }}>
        <Stack.Group>
          <Stack.Screen
            name="FormLogin"
            component={FormLogin}
            options={{ title: 'Formularz logowania' }} />
        </Stack.Group>
        <Stack.Group
          screenOptions={{
            presentation: 'modal',
            headerShown: false
          }}>
          <Stack.Screen
            name="BaseModal"
            component={BaseModal} />
        </Stack.Group>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
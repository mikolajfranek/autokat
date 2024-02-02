import { NavigationContainer } from '@react-navigation/native';
import { NativeStackScreenProps, createNativeStackNavigator } from '@react-navigation/native-stack';
import React from 'react';
import FormLogin from './Features/FormLogin';
import BaseModal from './Features/Modals/BaseModal';

const Stack = createNativeStackNavigator<RootStackParamList>();

type RootStackParamList = {
  FormLogin: undefined;
  BaseModal: { message: string } | undefined;
};

export type PropsOfFormLogin = NativeStackScreenProps<RootStackParamList, 'FormLogin', 'MainStack'>;
export type PropsOfBaseModal = NativeStackScreenProps<RootStackParamList, 'BaseModal', 'MainStack'>;

export default function App(): React.JSX.Element {
  return (
    <NavigationContainer>
      <Stack.Navigator
        id='MainStack'
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
            component={BaseModal}
            initialParams={{ message: "abc" }}
          />
        </Stack.Group>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
import { NavigationContainer } from '@react-navigation/native';
import { StackScreenProps, createStackNavigator } from '@react-navigation/stack';
import React, { useState } from 'react';
import FormLogin from './Features/FormLogin';
import BaseModal from './Features/Modals/BaseModal';
import MainTab from './Features/MainTab';

//Stack
const Stack = createStackNavigator<RootStackParamList>();
type RootStackParamList = {
  FormLogin: undefined;
  BaseModal: { message: string } | undefined;
  MainTab: undefined;
};
export type PropsOfFormLogin = StackScreenProps<RootStackParamList, 'FormLogin', 'MainStack'>;
export type PropsOfBaseModal = StackScreenProps<RootStackParamList, 'BaseModal', 'MainStack'>;

export default function App(): React.JSX.Element {
  //get from store
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  return (
    <NavigationContainer>
      <Stack.Navigator
        id='MainStack'
        initialRouteName="FormLogin"
        screenOptions={{
          headerStyle: { backgroundColor: 'lightblue' },
        }}>

        {isLoggedIn ? (
          // Screens for logged in users
          <Stack.Group>
            <Stack.Screen
              name="MainTab"
              component={MainTab}
              options={{ title: 'MainTab' }} />
          </Stack.Group>
        ) : (
          // Auth screens
          <Stack.Group>
            <Stack.Screen
              name="FormLogin"
              component={FormLogin}
              options={{ title: 'Formularz logowania' }} />
          </Stack.Group>
        )}

        {/* Common modal screens */}
        <Stack.Group screenOptions={{ presentation: 'modal' }}>
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
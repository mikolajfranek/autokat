import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { useAppSelector } from './hooks';
import MaterialBottomTab from './GUI/MaterialBottomTab';
import FormularzLogowania from './GUI/FormularzLogowania';
import ModalTmp from './GUI/Modale/ModalTmp';

const Stack = createStackNavigator();

export default function App(): React.JSX.Element {
  const authStatus = useAppSelector((state) => state.auth.status)
  return (
    <NavigationContainer>
      <Stack.Navigator id="Stack_Aplikacja">
        {
          authStatus
            ?
            <Stack.Screen
              name="Screen_MaterialBottomTab"
              component={MaterialBottomTab}
              options={{ title: "MaterialBottomTab" }} />
            :
            (
              <Stack.Screen
                name="Screen_FormularzLogowania"
                component={FormularzLogowania}
                options={{ headerShown: false }}
              />
            )
        }
        {/* presentation: {modal, transparentModal, card}  */}
        <Stack.Group screenOptions={{ presentation: 'modal' }}>
          <Stack.Screen
            name="Modal_Tmp"
            options={{ headerTitle: "ModalTmp" }}
            component={ModalTmp}
          />
        </Stack.Group>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
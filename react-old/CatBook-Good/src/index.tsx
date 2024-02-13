import { StackScreenProps, createStackNavigator } from "@react-navigation/stack";
import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { useAppSelector } from "./hooks";
import * as StyleBusiness from "./style_business";
import Tab_Aplikacja from "./Features/MainTab";
import Form_Logowanie from "./Features/FormLogin";
import Modal_OAplikacji from "./Features/Modals/OAplikacji";
import { UserStates } from "./Enums/UserStates";

const Stack = createStackNavigator<RootStackParamList>();

type RootStackParamList = {
  Tab_Aplikacja: undefined;
  Form_Logowanie: undefined;
  Modal_OAplikacji: undefined;
};
export type PropsOfTab_Aplikacja = StackScreenProps<RootStackParamList, "Tab_Aplikacja", "Stack_Aplikacja">;
export type PropsOfForm_Logowanie = StackScreenProps<RootStackParamList, "Form_Logowanie", "Stack_Aplikacja">;
export type PropsOfModal_OAplikacji = StackScreenProps<RootStackParamList, "Modal_OAplikacji", "Stack_Aplikacja">;

export default function App(): React.JSX.Element {
  const state = useAppSelector((state) => state.user.state)
  return (
    <NavigationContainer>
      <Stack.Navigator
        id="Stack_Aplikacja"
        screenOptions={{
          headerStyle: { backgroundColor: StyleBusiness.colorPrimary },
          headerTitleStyle: { color: StyleBusiness.colorWhite },
          headerTintColor: StyleBusiness.colorWhite,
          cardStyle: { backgroundColor: StyleBusiness.colorWhite }
        }}>
        {state === UserStates.LOGGED ? (
          // Screens for logged in users
          <Stack.Group>
            <Stack.Screen
              name="Tab_Aplikacja"
              component={Tab_Aplikacja}
              options={{ title: "CatBook" }} />
          </Stack.Group>
        ) : (
          // Auth screens
          <Stack.Group>
            <Stack.Screen
              name="Form_Logowanie"
              component={Form_Logowanie}
              options={{ title: "Formularz logowania" }} />
          </Stack.Group>
        )}
        {/* Common modal screens */}
        <Stack.Group screenOptions={{ presentation: "modal" }}>
          <Stack.Screen
            name="Modal_OAplikacji"
            options={{ headerTitle: "O aplikacji" }}
            component={Modal_OAplikacji}
          />
        </Stack.Group>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
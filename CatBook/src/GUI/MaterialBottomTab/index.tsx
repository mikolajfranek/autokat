import React, { useEffect, useRef, useState } from 'react';
import { createMaterialBottomTabNavigator } from 'react-native-paper/react-navigation';
import Start from '../Start';
import Ustawienia from '../Ustawienia';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import { AppState } from 'react-native';
import { loadUser } from '../../APIGoogle/Common';
import { LoginSheet, useGetLoginMutation } from '../../APIGoogle/APIDocs';
import { getLocalStorageString } from '../../LocalStorage';
import { LocalStorageKeys } from '../../Enums/LocalStorageKeys';
import { APISheetColumnOfTableLogin } from '../../Enums/APISheetColumnOfTableLogin';

const Tab = createMaterialBottomTabNavigator();

export default function App(): React.JSX.Element {
    const [getLogin] = useGetLoginMutation();
    //TODOs
    const appState = useRef(AppState.currentState);
    const [appStateVisible, setAppStateVisible] = useState(appState.current);
    useEffect(() => {
        const subscription = AppState.addEventListener('change', async nextAppState => {
            if (
                appState.current.match(/inactive|background/) &&
                nextAppState === 'active'
            ) {
                console.log('App has come to the foreground!');
                const loginSheet = JSON.parse(getLocalStorageString(LocalStorageKeys.loginSheet)) as LoginSheet;
                const loginSheet_login = loginSheet.c[APISheetColumnOfTableLogin.B_login].v;
                await loadUser(loginSheet_login, getLogin, undefined);
                //TODO
                // get isRecentlyCreated() {
                //     // in the last 4 hours
                //     return this.createdAt &&
                //         this.createdAt.getTime() > Date.now() - 1 * 4 * 3600 * 1000;
                // }
            }

            appState.current = nextAppState;
            setAppStateVisible(appState.current);
            console.log('AppState', appState.current);
        });

        return () => {
            subscription.remove();
        };
    }, []);

    return (
        <Tab.Navigator
            initialRouteName="Screen_MaterialBottomTab_Start">
            <Tab.Screen name="Screen_MaterialBottomTab_Start" component={Start}
                options={{
                    tabBarLabel: 'Start',
                    tabBarIcon: ({ color }) => (
                        <MaterialCommunityIcons name="home" color={color} size={26} />
                    ),
                }} />
            <Tab.Screen name="Screen_MaterialBottomTab_Ustawienia" component={Ustawienia}
                options={{
                    tabBarLabel: 'Ustawienia',
                    tabBarIcon: ({ color }) => (
                        <MaterialCommunityIcons name="cog" color={color} size={26} />
                    ),
                }} />
        </Tab.Navigator>
    );
}
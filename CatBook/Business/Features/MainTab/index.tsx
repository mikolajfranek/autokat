import React from 'react';
import Courses from '../Courses';
import ListOfCatalysts from '../ListOfCatalysts';
import Settings from '../Settings';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { useFocusEffect } from '@react-navigation/native';

//Tab
const Tab = createBottomTabNavigator();

export default function App({ navigation }): React.JSX.Element {
    useFocusEffect(
        React.useCallback(() => {
            // Do something when the screen is focused
            console.log("focus....");

            let license = "";
            if (license == "") {
                //navigation.navigate('FormLogin');
            }

            return () => {
                // Do something when the screen is unfocused
                console.log("clean....");
                // Useful for cleanup functions
            };
        }, []));

    return (
        <Tab.Navigator
            id='MainTab'
            initialRouteName="ListOfCatalysts">
            <Tab.Screen name="ListOfCatalysts" component={ListOfCatalysts} />
            <Tab.Screen name="Courses" component={Courses} />
            <Tab.Screen name="Settings" component={Settings} />
        </Tab.Navigator>
    );
}
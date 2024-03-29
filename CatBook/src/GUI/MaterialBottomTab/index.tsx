import React from 'react';
import { createMaterialBottomTabNavigator } from 'react-native-paper/react-navigation';
import Start from '../Start';
import Ustawienia from '../Ustawienia';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';

const Tab = createMaterialBottomTabNavigator();

export default function App(): React.JSX.Element {
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
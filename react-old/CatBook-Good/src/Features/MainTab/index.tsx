import React from 'react';
import Kursy from '../Kursy';
import Katalizatory from '../Katalizatory';
import Ustawienia from '../Ustawienia';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import * as StyleBusiness from '../../style_business';

const Tab = createBottomTabNavigator();

export default function App(): React.JSX.Element {
    return (
        <Tab.Navigator
            id='CatBook'
            initialRouteName="Katalizatory"
            screenOptions={{
                headerShown: false,
                tabBarInactiveTintColor: StyleBusiness.colorWhite,
                tabBarActiveTintColor: StyleBusiness.colorSucess,
                tabBarStyle: { backgroundColor: StyleBusiness.colorPrimary },
            }}>
            <Tab.Screen name="Katalizatory" component={Katalizatory} />
            <Tab.Screen name="Kursy" component={Kursy} />
            <Tab.Screen name="Ustawienia" component={Ustawienia} />
        </Tab.Navigator>
    );
}
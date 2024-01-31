
import * as React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import HomeScreen from './features/HomeScreen';
import SettingsScreen from './features/SettingsScreen';
import { NavigationContainer } from '@react-navigation/native';
import { Button, Text, View } from 'react-native';


const Tab = createBottomTabNavigator();


export default function MyTabs() {

    function ModalScreen({ navigation }) {
        return (
            <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
                <Text style={{ fontSize: 30 }}>This is a modal!</Text>
                <Button onPress={() => navigation.goBack()} title="Dismiss" />
            </View>
        );
    }

    return (
        <NavigationContainer>
            <Tab.Navigator
                screenOptions={{

                    headerShown: false
                }
                }
                initialRouteName="Home">


                <Tab.Screen name="Home" component={HomeScreen} />
                <Tab.Screen name="Settings" component={SettingsScreen} />

            </Tab.Navigator>
        </NavigationContainer>
    );
}
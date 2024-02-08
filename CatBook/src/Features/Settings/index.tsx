import React from 'react';
import { Button, Text, View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { logout } from '../../Slices/UserSlice';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch()
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                Settings
            </Text>
            <Button onPress={() => {
                dispatch(logout()); 
            }} title='Logout' />
        </View>
    );
}
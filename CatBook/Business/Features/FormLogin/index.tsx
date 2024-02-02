import React from 'react';
import { Button, Text, View } from 'react-native';
import { PropsOfFormLogin } from '../..';

export default function App({ navigation }: PropsOfFormLogin): React.JSX.Element {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                Form Login
            </Text>
            <Button onPress={() => navigation.push('BaseModal')} title='Push BaseModal' />
            <Button onPress={() => navigation.navigate('MainTab')} title='Navigate MainTab' />
        </View>
    );
}
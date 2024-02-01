import React from 'react';
import { Button, Text, View } from 'react-native';

export default function App({ navigation }): React.JSX.Element {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                Form Login
            </Text>
            <Button onPress={() => navigation.push('BaseModal')} title='Push BaseModal' />
        </View>
    );
}
import React from 'react';
import { Button, Text, View } from 'react-native';
import { PropsOfBaseModal } from '../../..';

export default function App({ navigation, route }: PropsOfBaseModal): React.JSX.Element {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                BaseModal with message {route.params?.message}
            </Text>
            <Button onPress={() => navigation.goBack()} title="Dismiss goBack" />
            <Button onPress={() => navigation.popToTop()} title="Dismiss popToTop" />
        </View>
    );
}
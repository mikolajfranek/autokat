import React from 'react';
import { Button, Text, View } from 'react-native';
import { Props } from '../../..';

export default function App({ navigation } : Props ): React.JSX.Element {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                BaseModal
            </Text>
            <Button onPress={() => navigation.goBack()} title="Dismiss goBack" />
            <Button onPress={() => navigation.popToTop()} title="Dismiss popToTop" />
        </View>
    );
}
import React from 'react';
import { View } from 'react-native';
import { Text } from 'react-native-paper';

export default function App(): React.JSX.Element {
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello KursyMetali
            </Text>
        </View>
    );
}
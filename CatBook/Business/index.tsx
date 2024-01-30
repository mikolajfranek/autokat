import React from 'react';
import {
    Platform,

} from 'react-native';


import { View, Text } from '@gluestack-ui/themed';

const message = Platform.select({
    ios: `Hello world in IOS ${Platform.Version}`,
    android: `Hello world in Android ${Platform.Version}`,
});

export default function render(): React.JSX.Element {
    return (
        <View>
            <Text>
                {message}
            </Text>
        </View>
    );
}
import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { Button, Text, View } from 'react-native';

export default function App(): React.JSX.Element {
    const navigation = useNavigation();
    return (
        <View style={{ flex: 2 }}>
            <View
                style={{ flex: 1, justifyContent: 'center' }}>
                <Text style={{ alignSelf: 'center' }} >
                    Hello CatBook ModalTmp
                </Text>
            </View>
            <View
                style={{ flex: 1, width: 100, alignSelf: 'center' }}>
                <Button
                    onPress={() => navigation.goBack()}
                    title="OK" />
            </View>
        </View>
    );
}
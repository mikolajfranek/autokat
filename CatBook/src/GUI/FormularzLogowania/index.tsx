import React from 'react';
import { Button, Text, View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { setAuthStatus } from '../../Slices/Auth';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook FormularzLogowania
            </Text>
            <Button
                onPress={async () => {
                    //TODO
                    dispatch(setAuthStatus(true));
                }}
                title="Zaloguj się" />
        </View>
    );
}
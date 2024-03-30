import React from 'react';
import { Text, View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { setAuthenticated } from '../../Slices/Auth';
import { Button } from 'react-native-paper';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook FormularzLogowania
            </Text>
            <Button
                icon='login'
                onPress={async () => {
                    //TODO
                    dispatch(setAuthenticated(true));
                }}>
                Zaloguj się
            </Button>
        </View>
    );
}
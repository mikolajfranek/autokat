import React from 'react';
import { Image, View } from 'react-native';
import { setAuthStatus } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Text } from 'react-native-paper';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook Ustawienia
            </Text>

            <Button icon={require('./chameleon.jpg')}
                theme={{ colors: { primary: undefined } }} >
                Is black text
                <Text>Success too</Text>
            </Button>

            <Button
                icon={() => (
                    <Image
                        source={require('./chameleon.jpg')}
                        style={{ width: 100, height: 100 }}
                    />
                )}
            >
                Success
            </Button>
            <Button
                icon='logout'
                onPress={async () => {
                    //TODO
                    dispatch(setAuthStatus(false))
                }}>
                Wyloguj się
            </Button>
        </View>
    );
}
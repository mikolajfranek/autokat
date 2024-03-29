import React from 'react';
import { Image, Text, View } from 'react-native';
import { setAuthStatus } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button } from 'react-native-paper';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook BottomTab
            </Text>

            <Button icon={require('./chameleon.jpg')}>
                Failed
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
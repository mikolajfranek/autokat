import React from 'react';
import { Button, Text, View } from 'react-native';
import { setAuthStatus } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook BottomTab
            </Text>
            <Button
                onPress={async () => {
                    //TODO
                    dispatch(setAuthStatus(false))
                }}
                title="Wyloguj się" />
        </View>
    );
}
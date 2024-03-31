import React, { useState } from 'react';
import { View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { setAuthenticated } from '../../Slices/Auth';
import { Button, Text, TextInput } from 'react-native-paper';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const [username, setUsername] = useState('');
    const [message, setMessage] = useState('');
    return (
        <View style={{ flex: 2, margin: 10 }}>
            <View style={{ flex: 1, alignSelf: 'center', justifyContent: 'center' }}>
                <Text variant='displayLarge'>
                    CatBook
                </Text>
            </View>
            <View style={{ flex: 1, justifyContent: 'center' }}>
                <TextInput
                    placeholder='Login'
                    onChangeText={newText => setUsername(newText)}
                />
                <Text style={{ margin: 10 }}>
                    {message}
                </Text>
                <Button
                    icon='login'
                    onPress={async () => {
                        try {
                            setMessage('Trwa uwierzytelnianie...');



                            //TODO

                            dispatch(setAuthenticated(true));
                            setMessage('');
                        } catch (error) {
                            setMessage('Wystąpił błąd');
                        }
                    }}>
                    Zaloguj się
                </Button>
            </View>
        </View>
    );
}
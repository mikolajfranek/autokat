import React, { useState } from 'react';
import { View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { setAuthenticated } from '../../Slices/Auth';
import { Button, Text, TextInput } from 'react-native-paper';
import { loadUser } from '../../APIGoogle/Common';
import { useGetLoginMutation } from '../../APIGoogle/APIDocs';
import { usePutUidMutation } from '../../APIGoogle/APISheets';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const [login, setLogin] = useState('');
    const [message, setMessage] = useState('');
    const [getLogin] = useGetLoginMutation();
    const [putUid] = usePutUidMutation();
    return (
        <View style={{ flex: 2, margin: 10 }}>
            <View style={{ flex: 1, alignSelf: 'center', justifyContent: 'center' }}>
                <Text variant='displayLarge'>
                    AUTO-KAT
                </Text>
            </View>
            <View style={{ flex: 1, justifyContent: 'center' }}>
                <TextInput
                    placeholder='Login'
                    onChangeText={newText => setLogin(newText)}
                />
                <Text style={{ margin: 10, alignSelf: 'center' }}>
                    {message}
                </Text>
                <Button
                    icon='login'
                    onPress={async () => {
                        try {
                            setMessage('Trwa uwierzytelnianie...');
                            await loadUser(login, getLogin, putUid);
                            dispatch(setAuthenticated(true));
                            setMessage('');
                        } catch (error) {
                            //TODO
                            //console.log(error);
                            setMessage('Wystąpił błąd');
                        }
                    }}>
                    Zaloguj się
                </Button>
            </View>
        </View>
    );
}
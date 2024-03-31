import React, { useState } from 'react';
import { View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { setAuthenticated } from '../../Slices/Auth';
import { Button, Text, TextInput } from 'react-native-paper';
import { useGetLoginMutation } from '../../APIGoogle/APIDocs';
import { APISheetColumnOfTableLogin } from '../../Enums/APISheetColumnOfTableLogin';
import { getUniqueId } from 'react-native-device-info';
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
                    CatBook
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
                            if (login.length == 0)
                                throw Error();
                            const user = await getLogin({ login: login }).unwrap();
                            const user_base = user.table.rows[0];
                            const user_id = user_base.c[APISheetColumnOfTableLogin.A_id];
                            const user_uid = user.table.rows[0].c[APISheetColumnOfTableLogin.C_uuid];
                            const uid = await getUniqueId();
                            if (user_uid != null) {
                                if (user_uid.v != uid)
                                    throw Error();
                            } else {
                                const result = await putUid({ id: user_id.v, uid: uid }).unwrap();
                                if (result.updatedCells != 1 || result.updatedColumns != 1 || result.updatedRows != 1)
                                    throw Error();
                            }
                            console.log('hurra');
                            //save user to localstore
                            //TODO
                            throw Error();

                            dispatch(setAuthenticated(true));
                            setMessage('');
                        } catch (error) {
                            console.log(error);
                            setMessage('Wystąpił błąd');
                        }
                    }}>
                    Zaloguj się
                </Button>
            </View>
        </View>
    );
}
import React from 'react';
import { Button, Text, View } from 'react-native';
import { PropsOfFormLogin } from '../..';
import { useAppDispatch } from '../../hooks';
import { loginUser } from '../../Slices/UserSlice';


export default function App({ navigation }: PropsOfFormLogin): React.JSX.Element {

    const dispatch = useAppDispatch()
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
            <Text>
                Form Login
            </Text>
            <Button onPress={() => navigation.push('BaseModal')} title='Push BaseModal' />
            <Button onPress={() => {
                dispatch(loginUser()); //async

                //in other case use navigate, after properly before actions...of async
                //navigation.navigate('MainTab');

            }} title='Navigate MainTab' />
        </View>
    );
}
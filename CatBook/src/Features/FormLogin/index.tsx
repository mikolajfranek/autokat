import React, { useState } from 'react';
import { TextInput, View } from 'react-native';
import { PropsOfForm_Logowanie } from '../..';
import { useAppDispatch, useAppSelector } from '../../hooks';
import { autoLogin, login } from '../../Slices/UserSlice';
import Button from '../../Backend/GUI/Buttons/MyBaseButtonViewTouchableHighlight';
import styles from './style';
import MyBaseText from '../../Backend/GUI/Texts/MyBaseText';
import * as StyleBusiness from '../../style_business';
import { useFocusEffect } from '@react-navigation/native';
import { UserStates } from '../../Enums/UserStates';
import { OperationStates } from '../../Enums/OperationStates';

export default function App({ navigation }: PropsOfForm_Logowanie): React.JSX.Element {
    //
    const state = useAppSelector((state) => state.user.state)
    const operationState = useAppSelector((state) => state.user.operationState)
    const dispatch = useAppDispatch()
    useFocusEffect(
        React.useCallback(() => {
            // Do something when the screen is focused
            pressLogin(true);
            return () => {
                // Do something when the screen is unfocused
                // Useful for cleanup functions
            };
        }, []));
    //state
    const [inputLogin, setInputLogin] = useState('');
    const [inputCompany, setInputCompany] = useState('');
    const [inputPass, setInputPass] = useState('');
    //render
    function pressLogin(autologin: boolean) {
        if (autologin === true) {
            dispatch(autoLogin());
            return;
        }
        dispatch(login({
            loginInput: inputLogin,
            companyInput: inputCompany,
            passInput: inputPass,
            processInput: state == UserStates.NOT_LOGGED_NEED_PASS
        }));
    }
    let textInputCompany = null;
    if (state >= UserStates.NOT_LOGGED_NEED_COMPANY) {
        textInputCompany = (
            <TextInput
                style={styles.textInput}
                placeholder='Firma:'
                onChangeText={newText => setInputCompany(newText)}
                defaultValue={inputCompany}
            />
        );
    }
    let textInputPass = null;
    if (state >= UserStates.NOT_LOGGED_NEED_PASS) {
        textInputPass = (
            <TextInput
                style={styles.textInput}
                placeholder='Hasło:'
                onChangeText={newText => setInputPass(newText)}
                defaultValue={inputPass}
                secureTextEntry={true}
            />
        );
    }
    let feedback = null;
    switch (operationState) {
        case OperationStates.IDLE:
        default:
            //
            break;
        case OperationStates.LOADING:
            feedback = (
                <MyBaseText styleText={{ paddingVertical: 10 }}>
                    Trwa logowanie...
                </MyBaseText>);
            break;
        case OperationStates.FAILED:
            feedback = (
                <MyBaseText styleText={{ color: StyleBusiness.colorDanger, paddingVertical: 10 }}>
                    Wystąpił błąd
                </MyBaseText>);
            break;
    }
    return (
        <View style={styles.container}>
            <View style={styles.form}>
                <TextInput
                    style={styles.textInput}
                    placeholder='Login:'
                    onChangeText={newText => setInputLogin(newText)}
                    defaultValue={inputLogin}
                />
                {textInputCompany}
                {textInputPass}
                {feedback}
                <Button
                    onPress={() => pressLogin(false)}>
                    <MyBaseText styleText={{ color: StyleBusiness.colorWhite }}>
                        Zaloguj się
                    </MyBaseText>
                </Button>
            </View>
            <View
                style={styles.about}>
                <Button
                    styleView={{ backgroundColor: StyleBusiness.colorSecondary }}
                    onPress={() => navigation.push('Modal_OAplikacji')} >
                    <MyBaseText
                        styleText={{ color: StyleBusiness.colorWhite }}>
                        O aplikacji
                    </MyBaseText>
                </Button>
            </View>
        </View>
    );
}
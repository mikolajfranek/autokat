import React from 'react';
import { View } from 'react-native';
import { useAppDispatch } from '../../hooks';
import { logout } from '../../Slices/UserSlice';
import Button from '../../Backend/GUI/Buttons/MyBaseButtonViewTouchableHighlight';
import MyBaseText from '../../Backend/GUI/Texts/MyBaseText';
import * as StyleBusiness from '../../style_business';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch()
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: StyleBusiness.colorWhite }}>
            <MyBaseText>
                Ustawienia
            </MyBaseText>
            <Button
                onPress={() => dispatch(logout())} >
                <MyBaseText
                    styleText={{ color: StyleBusiness.colorWhite }}>
                    Wyloguj
                </MyBaseText>
            </Button>
        </View>
    );
}
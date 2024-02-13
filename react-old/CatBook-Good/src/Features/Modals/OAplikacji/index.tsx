import React from 'react';
import { PropsOfModal_OAplikacji } from '../../..';
import { View } from 'react-native';
import styles from './style';
import MyBaseText from '../../../Backend/GUI/Texts/MyBaseText';
import Button from '../../../Backend/GUI/Buttons/MyBaseButtonViewTouchableHighlight';
import * as StyleBusiness from '../../../style_business';

export default function App({ navigation }: PropsOfModal_OAplikacji): React.JSX.Element {
    return (
        <View style={styles.container}>
            <MyBaseText>
                Informacje wyświetlane są w formie 'modala'
            </MyBaseText>
            <Button
                onPress={() => navigation.goBack()}>
                <MyBaseText
                    styleText={{ color: StyleBusiness.colorWhite }}>
                    OK
                </MyBaseText>
            </Button>
        </View>
    );
}
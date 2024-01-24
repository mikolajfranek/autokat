import React from 'react';
import { Text, View, TouchableHighlight, StyleProp, ViewStyle, GestureResponderEvent, TextStyle } from 'react-native';
import styles from './style';

type MyButtonProps = {
    onPress: ((event: GestureResponderEvent) => void) | undefined;
    label: string;
    styleTouchable?: StyleProp<ViewStyle>;
    styleButton?: StyleProp<ViewStyle>;
    styleText?: StyleProp<TextStyle>;
};

export default function render(props: MyButtonProps) {
    return (
        <TouchableHighlight
            style={[styles.touchable, props.styleTouchable]}
            onPress={props.onPress}>
            <View style={[styles.button, props.styleButton]}>
                <Text style={[styles.text, props.styleText]}>
                    {props.label}
                </Text>
            </View>
        </TouchableHighlight>
    );
}
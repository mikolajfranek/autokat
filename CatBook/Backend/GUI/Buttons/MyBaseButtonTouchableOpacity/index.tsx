import React, { PropsWithChildren } from 'react';
import { GestureResponderEvent, TouchableOpacity, StyleProp, ViewStyle } from 'react-native';
import styles from './style';

type MyBaseButtonProps = {
    onPress: ((event: GestureResponderEvent) => void) | undefined;
    styleTouchableOpacity?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyBaseButtonProps>) {
    return (
        <TouchableOpacity
            style={[styles.touchable, props.styleTouchableOpacity]}
            onPress={props.onPress}>
            {props.children}
        </TouchableOpacity>
    );
}
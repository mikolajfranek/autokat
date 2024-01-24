import React, { PropsWithChildren } from 'react';
import { View, StyleProp, ViewStyle, GestureResponderEvent, TouchableHighlight } from 'react-native';
import styles from './style';

type MyButtonProps = {
    onPress: ((event: GestureResponderEvent) => void) | undefined;
    styleTouchable?: StyleProp<ViewStyle>;
    styleView?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyButtonProps>) {
    return (
        <TouchableHighlight
            style={[styles.touchable, props.styleTouchable]}
            onPress={props.onPress}>
            <View style={[styles.button, props.styleView]}>
                {props.children}
            </View>
        </TouchableHighlight>
    );
}
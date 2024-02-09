import React, { PropsWithChildren } from "react";
import { View, StyleProp, ViewStyle, GestureResponderEvent, TouchableHighlight } from "react-native";
import styles from "./style";

type MyButtonProps = {
    onPress: ((event: GestureResponderEvent) => void) | undefined;
    styleTouchableHighlight?: StyleProp<ViewStyle>;
    styleView?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyButtonProps>) {
    return (
        <TouchableHighlight
            style={[styles.touchable, props.styleTouchableHighlight]}
            onPress={props.onPress}>
            <View style={[styles.view, props.styleView]}>
                {props.children}
            </View>
        </TouchableHighlight>
    );
}
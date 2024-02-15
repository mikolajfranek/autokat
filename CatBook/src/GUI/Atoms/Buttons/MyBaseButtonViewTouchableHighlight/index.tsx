import React, { PropsWithChildren, useState } from "react";
import { View, StyleProp, ViewStyle, GestureResponderEvent, TouchableHighlight } from "react-native";
import styles from "./style";

type MyButtonProps = {
    onPress: ((event: GestureResponderEvent) => Promise<void>);
    styleTouchableHighlight?: StyleProp<ViewStyle>;
    styleView?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyButtonProps>) {
    const [isDisabled, setDisabled] = useState(false);
    return (
        <TouchableHighlight
            style={[styles.touchable, props.styleTouchableHighlight]}
            disabled={isDisabled}
            onPress={async (event) => {
                setDisabled(true);
                try {
                    await props.onPress(event);
                } finally {
                    setDisabled(false);
                }
            }}>
            <View style={[styles.view, props.styleView]}>
                {props.children}
            </View>
        </TouchableHighlight>
    );
}
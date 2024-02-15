import React, { PropsWithChildren, useState } from "react";
import { GestureResponderEvent, TouchableOpacity, StyleProp, ViewStyle } from "react-native";
import styles from "./style";

type MyBaseButtonProps = {
    onPress: ((event: GestureResponderEvent) => Promise<void>);
    styleTouchableOpacity?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyBaseButtonProps>) {
    const [isDisabled, setDisabled] = useState(false);
    return (
        <TouchableOpacity
            style={[styles.touchable, props.styleTouchableOpacity]}
            disabled={isDisabled}
            onPress={async (event) => {
                setDisabled(true);
                try {
                    await props.onPress(event);
                } finally {
                    setDisabled(false);
                }
            }}>
            {props.children}
        </TouchableOpacity>
    );
}
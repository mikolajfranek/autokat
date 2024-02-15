import React, { PropsWithChildren, useState } from "react";
import { TouchableOpacity, StyleProp, ViewStyle } from "react-native";
import styles from "./style";

type MyBaseButtonProps = {
    onPress: (() => Promise<void>);
    styleTouchableOpacity?: StyleProp<ViewStyle>;
};

export default function render(props: PropsWithChildren<MyBaseButtonProps>) {
    const [isDisabled, setDisabled] = useState(false);
    return (
        <TouchableOpacity
            style={[styles.touchable, props.styleTouchableOpacity]}
            disabled={isDisabled}
            onPress={async () => {
                setDisabled(true);
                try {
                    await props.onPress();
                } finally {
                    setDisabled(false);
                }
            }}>
            {props.children}
        </TouchableOpacity>
    );
}
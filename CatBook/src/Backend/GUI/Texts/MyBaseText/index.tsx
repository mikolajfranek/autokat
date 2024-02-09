import React, { PropsWithChildren } from "react";
import { Text, StyleProp, TextStyle } from "react-native";
import styles from "./style";

type MyBaseTextProps = {
    styleText?: StyleProp<TextStyle>;
};

export default function render(props: PropsWithChildren<MyBaseTextProps>) {
    return (
        <Text style={[styles.text, props.styleText]}>
            {props.children}
        </Text>
    );
}
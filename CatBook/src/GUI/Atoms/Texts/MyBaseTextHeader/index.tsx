import React, { PropsWithChildren } from "react";
import { StyleProp, Text, TextStyle } from "react-native";
import styles from "./style";

type MyHeaderTextProps = {
    styleText?: StyleProp<TextStyle>;
};

export default function render(props: PropsWithChildren<MyHeaderTextProps>) {
    return (
        <Text style={[styles.text, props.styleText]}>
            {props.children}
        </Text>
    );
}
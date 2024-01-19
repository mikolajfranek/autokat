import React from "react";
import { Text, View, TouchableHighlight } from "react-native";
import styles from "./style";

export default function render({ onPress, label, styleTouchable, styleButton, styleText }) {
    return (
        <TouchableHighlight
            style={[styles.touchable, styleTouchable]}
            onPress={onPress}>
            <View style={[styles.button, styleButton]}>
                <Text style={[styles.text, styleText]}>
                    {label}
                </Text>
            </View>
        </TouchableHighlight>
    );
}
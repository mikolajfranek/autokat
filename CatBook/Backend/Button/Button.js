import React from "react";

import { Text, View, TouchableHighlight } from "react-native";

import styles from "./style";

export default function render({ onPress, style, label }) {
  return (
    <TouchableHighlight onPress={onPress}>
      <View style={[styles.button, style]}>
        <Text>
          {label}
        </Text>
      </View>
    </TouchableHighlight>
  );
}
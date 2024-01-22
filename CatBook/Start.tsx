import React from 'react';
import {
  Platform,
  Text,
  View,
} from 'react-native';
import styles from "./style";
import Login from './Business/GUI/Login';

const message = Platform.select({
  ios: 'Hello world in IOS',
  android: 'Hello world in Android'
});

export default function render(): React.JSX.Element {
  return (
    <View style={styles.container}>
      <View style={styles.welcome}>
        <Text style={styles.text}>
          {message}
        </Text>
      </View>
      <Login />
    </View>
  );
}
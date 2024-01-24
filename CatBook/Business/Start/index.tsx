import React from 'react';
import {
  Platform,
  Text,
  View,
} from 'react-native';
import styles from "./style";
import Login from '../GUI/FormLogin';

const message = Platform.select({
  ios: `Hello world in IOS ${Platform.Version}`,
  android: `Hello world in Android ${Platform.Version}`,
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
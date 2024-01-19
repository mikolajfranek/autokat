/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
} from 'react-native';
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ddd',
  },
  welcome: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 20,
    margin: 10,
    color: '#333',
  },
});
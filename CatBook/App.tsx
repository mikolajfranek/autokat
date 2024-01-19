/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  StyleSheet,
  Text,
  View,
} from 'react-native';

export default function App(): React.JSX.Element {
  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>
        Hello world!
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', backgroundColor: 'white' },
  welcome: {textAlign: 'center', },
});
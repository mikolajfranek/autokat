import React, { Component } from 'react';
import {
  Platform,
  View,
  StyleSheet,
  Text
} from 'react-native';

const operatingSystem = Platform.select({
  ios: 'iOS',
  android: 'Android'
})

export default class App extends Component<{}> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Hello world in {operatingSystem}!
        </Text>
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'lightblue',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
  },
});
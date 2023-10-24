import React from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import KoloPrzycisk from './Shared/TouchableHighlights/BasicButton';
import KoloPrzesuwajace from './Shared/PanResponders/BasicButton';

export default function render() {
  return (
    <View style={styles.container}>
      <KoloPrzycisk />
      <KoloPrzesuwajace />
      <Text>
        Hello world!
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
});
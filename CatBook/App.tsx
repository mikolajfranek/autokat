import React from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import KoloPrzycisk from './Shared/TouchableHighlights/BasicButton';
import KoloPrzesuwajace from './Shared/PanResponders/BasicButton';
import ModelCatalyst from './Business/Models/Catalyst';

const hide = false;

export default function render() {
  const model = new ModelCatalyst(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
  console.log('Assigned id is ' + model.id);
  return (
    <View style={styles.container}>
      {hide ? <KoloPrzycisk /> : ''}
      {hide ? <KoloPrzesuwajace /> : ''}
      {hide ? <Text>Hello world!</Text> : ''}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
});
import React from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import KoloPrzycisk from './Shared/TouchableHighlights/BasicButton';
import KoloPrzesuwajace from './Shared/PanResponders/BasicButton';
import UICatalystFlatList from './Business/UI/CatalystFlatList';
import Przycisk from './Backend/UI/Button';

const hide = false;

export default function render() {

  return (
    <View style={styles.container}>
      {hide ? <KoloPrzycisk /> : ''}
      {hide ? <KoloPrzesuwajace /> : ''}
      {hide ? <Text>Hello world!</Text> : ''}
      {hide ? <UICatalystFlatList /> : ''}
      <Text>---</Text>
      <Przycisk 
        label={"hejooo"}
        onPress={null}
        style={null}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
});
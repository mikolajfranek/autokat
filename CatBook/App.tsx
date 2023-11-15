import React from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import KoloPrzycisk from './Shared/TouchableHighlights/BasicButton';
import KoloPrzesuwajace from './Shared/PanResponders/BasicButton';
import UICatalystFlatList from './Business/UI/CatalystFlatList';
import Button from './Backend/Button/Button'

const hide = false;

export default function render() {

  return (
    <View style={styles.container}>
      {hide ? <KoloPrzycisk /> : ''}
      {hide ? <KoloPrzesuwajace /> : ''}
      {hide ? <Text>Hello world!</Text> : ''}
      {hide ? <UICatalystFlatList /> : ''}
      <Button 
        label={"hejo"}
        onPress={null}
        style={null}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
});
import React from 'react';
import {
  Text,
  View,
} from 'react-native';
import KoloPrzycisk from './Shared/TouchableHighlights/BasicButton';

export default function render() {
  return (
    <View>
      <KoloPrzycisk />
      <Text>
        Hello world!
      </Text>
    </View>
  );
}
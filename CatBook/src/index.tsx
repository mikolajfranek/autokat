import React from 'react';
import {
  Text,
  View,
} from 'react-native';

export default function App(): React.JSX.Element {
  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
      <Text style={{ alignSelf: 'center' }} >
        Hello CatBook
      </Text>
    </View>
  );
}
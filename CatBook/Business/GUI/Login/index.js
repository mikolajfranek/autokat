import React, { useState } from 'react';
import {
  Text,
  View,
} from 'react-native';
import styles from "./style";
import Button from '../../../Backend/GUI/MyButton';

export default function render() {
  const [iterator, setIterator] = useState(0);

  function pressLogin() {
    setIterator(iterator + 1);
  }

  return (
    <View style={styles.container}>
      <Text>
        Formularz logowania {iterator}
      </Text>
      <Button
        onPress={pressLogin}
        label="Akcja"
        styleButton={{ backgroundColor: 'red' }}
      />
    </View>
  );
}
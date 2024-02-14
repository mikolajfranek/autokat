import React, { useState } from 'react';
import {
  Button,
  Text,
  View,
} from 'react-native';
import { database } from './Database/DBA';

export interface PromiseParams {
  result: boolean;
}

function getPromise(): Promise<PromiseParams> {
  return new Promise<PromiseParams>((resolve) =>
    setTimeout(() => {
      resolve(
        {
          result: true
        });
    }, 4000)
  );
}

export default function App(): React.JSX.Element {
  const [amount, setAmount] = useState('');
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>
        Hello world! {amount}
      </Text>
      <Button
        onPress={async () => {
          await getPromise();
          var increment = Number(await database.localStorage.get("increment"));
          increment = (increment ? increment : 0) + 1;
          await database.localStorage.set("increment", increment);
          setAmount(increment.toString());
        }}
        title='Kliknij mnie'
      />
    </View>
  );
}
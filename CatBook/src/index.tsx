import React, { useState } from 'react';
import {
  Button,
  Text,
  View,
} from 'react-native';
import { database } from './Database/DBA';
import { useAppDispatch } from './hooks';
import { loginAsync } from './Slices/User';
import MyBaseButtonViewTouchableHighlight from './GUI/Atoms/Buttons/MyBaseButtonViewTouchableHighlight';
import MyBaseText from './GUI/Atoms/Texts/MyBaseText';
import { colorTextWhite } from './GUI/gui_style';
import { useGetEURQuery, useGetUSDQuery } from './APIExchange';

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
    }, 2000)
  );
}

export default function App(): React.JSX.Element {
  const [amount, setAmount] = useState('');
  const dispatch = useAppDispatch();
  const {
    data: dataUSD,
    isSuccess: isSuccessUSD
  } = useGetUSDQuery({ dataKursu: new Date(Date.now() - 86400000).toLocaleDateString('sv-SE') });
  let usdElement = null;
  if (isSuccessUSD) {
    usdElement = <Text>{dataUSD.rates[0].effectiveDate} {dataUSD.rates[0].mid} USD</Text>
  }

  const {
    data: dataEUR,
    isSuccess: isSuccessEUR
  } = useGetEURQuery();
  let eurElement = null;
  if (isSuccessEUR) {
    eurElement = <Text>{dataEUR.rates[0].mid} EUR</Text>
  }


  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      {usdElement}
      {eurElement}
      <Text>
        Hello world! {amount}
      </Text>
      <MyBaseButtonViewTouchableHighlight
        onPress={async () => {
          await getPromise();
          var increment = Number(await database.localStorage.get("increment"));
          increment = (increment ? increment : 0) + 1;
          await database.localStorage.set("increment", increment);
          setAmount(increment.toString());
        }}>
        <MyBaseText
          styleText={{ color: colorTextWhite }}>
          Zaloguj się - own component
        </MyBaseText>
      </MyBaseButtonViewTouchableHighlight>
      <Button
        title={'Zaloguj się - dispatch'}
        onPress={() => dispatch(loginAsync())}
      />
      <Button
        title={'Zaloguj się - dispatch'}
        onPress={() => dispatch(loginAsync()).unwrap()
          .then((originalPromiseResult) => {
            // handle result here
          })
          .catch((rejectedValueOrSerializedError) => {
            // handle error here
          })}
      />
    </View>
  );
}
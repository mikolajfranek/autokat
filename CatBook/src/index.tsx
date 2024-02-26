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
import { useGetCoursesQuery } from './APIMetal';
import { useGetLoginQuery } from './APIDocsGoogle';
import { getSpreadsheetLoginId } from './APIDocsGoogle/Secret';
import { APISheetColumnOfTableLogin } from './Enums/APISheetColumnOfTableLogin';

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
  } = useGetUSDQuery();
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
    eurElement = <Text>{dataEUR.rates[0].effectiveDate} {dataEUR.rates[0].mid} EUR</Text>
  }

  const OZ_VALUE = '31.1034768';
  const {
    data: dataMetals,
    isSuccess: isSuccessMetals
  } = useGetCoursesQuery();
  let platinumElement = null;
  if (isSuccessMetals) {
    platinumElement = <Text>{dataMetals.data.platinum.results[0].bid / OZ_VALUE} platinum USD/gram</Text>
  }

  //TODO apiSheet
  const {
    data: dataTableLogin,
    isSuccess: isSuccessTableLogin,
    isError,
    error
  } = useGetLoginQuery(
    {
      spreadsheetId: getSpreadsheetLoginId(),
      authParams: {
        mail: 'mikolaj.franek95@gmail.com',
        serialID: ''
      }
    }
  );
  let loginElement = null;
  if (isSuccessTableLogin) {
    console.log(dataTableLogin.table.rows[0])
    loginElement = <Text>{dataTableLogin.table.rows[0].c[APISheetColumnOfTableLogin.password].v}</Text>;
  } else if (isError) {
    console.log(error);
    loginElement = <Text>{JSON.stringify(error)}</Text>;
  }


  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      {loginElement}
      {usdElement}
      {eurElement}
      {platinumElement}
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
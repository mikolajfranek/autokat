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
import { useGetExchangeMutation } from './APIExchange';
import { useGetMetalsMutation } from './APIMetal';
import { useGetLoginQuery } from './APIDocsGoogle';
import { APISheetColumnOfTableLogin } from './Enums/APISheetColumnOfTableLogin';

export const IS_PRODUCTION = false;

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
  // const [
  //   getUSD, {
  //     isSuccess: isSuccessUSD }
  // ] = useGetExchangeMutation();
  // const dataUSD = null;//getUSD({ currency: 'usd' });
  let usdElement = null;
  // if (isSuccessUSD) {
  //   usdElement = <Text>{dataUSD.rates[0].effectiveDate} {dataUSD.rates[0].mid} USD</Text>
  // }

  const [
    getEUR, { isSuccess: isSuccessEUR }] = useGetExchangeMutation();
  const dataEUR = null;
  let eurElement = null;
  //if (isSuccessEUR) {
  //eurElement = <Text>{dataEUR.rates[0].effectiveDate} {dataEUR.rates[0].mid} EUR</Text>
  //}


  const [
    getMMeeeetal, { isLoading }] = useGetMetalsMutation();



  const OZ_VALUE = 31.1034768;

  let platinumElement = null;
  //if (isSuccessMetals) {
  //platinumElement = <Text>{dataMetals.data.platinum.results[0].bid / OZ_VALUE} platinum USD/gram</Text>
  //}



  //TODO apiSheet
  const {
    data: dataTableLogin,
    isSuccess: isSuccessTableLogin,
    isError,
    error
  } = useGetLoginQuery({})
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

          try {
            let rrrr = await getMMeeeetal().unwrap();
            console.log(`${rrrr.data.rhodium.results[0].bid / OZ_VALUE} platinum USD/gram`);
          } catch (error) {
            //TODO
            console.error('rejected', error);
          }
        }}>
        <MyBaseText
          styleText={{ color: colorTextWhite }}>
          Pobierz..metal
        </MyBaseText>

      </MyBaseButtonViewTouchableHighlight>

      <MyBaseButtonViewTouchableHighlight
        onPress={async () => {

          try {
            //var yesterday = new Date(Date.now() - 86400000*3);
            //console.log(yesterday.toLocaleDateString('sv-SE'));
            let data = await getEUR({ currency: 'eur' }).unwrap();
            //console.log(data);
            //console.log(data.rates[0].mid);
            //console.log(data.rates[0].effectiveDate);
          } catch (error) {
            //TODO
            console.error('rejected', error);
          }
        }}>
        <MyBaseText
          styleText={{ color: colorTextWhite }}>
          Pobierz..waluta
        </MyBaseText>

      </MyBaseButtonViewTouchableHighlight>

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
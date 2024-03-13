import React from 'react';
import {
  Text,
  View,
} from 'react-native';
import { useGetLoginQuery } from './APIGoogle/APIDocs';
import { APISheetColumnOfTableLogin } from './Enums/APISheetColumnOfTableLogin';

export default function App(): React.JSX.Element {
  const {
    data: dataTableLogin,
    isSuccess: isSuccessTableLogin,
    isError,
    error
  } = useGetLoginQuery({})
  let loginElement = null;
  if (isSuccessTableLogin) {
    console.log(JSON.stringify(dataTableLogin.table.rows))
    loginElement = <Text>{dataTableLogin.table.rows[0].c[APISheetColumnOfTableLogin.password].v}</Text>;
  } else if (isError) {
    console.log(error);
    loginElement = <Text>{JSON.stringify(error)}</Text>;
  }

  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
      <Text style={{ alignSelf: 'center' }} >
        Hello CatBook
      </Text>
    </View>
  );
}
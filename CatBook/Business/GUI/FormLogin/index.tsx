import React, { useState } from 'react';
import {
  Text,
  TextInput,
  View,
} from 'react-native';
import styles from './style';
import Button from '../../../Backend/GUI/MyButton';
import * as Spreadsheet from '../../Spreadsheet/Login';

enum StateOfLogin {
  NeedLogin,
  NeedCompany,
  NeedPassword,
  Loged,
};

export default function render(): React.JSX.Element {
  const [state, setState] = useState(StateOfLogin.NeedLogin);
  const [inputLogin, setInputLogin] = useState('');
  const [inputCompany, setInputCompany] = useState('');
  const [inputPass, setInputPass] = useState('');
  const [infoMessage, setInfoMessage] = useState('');

  function pressLogin() {
    switch (state) {
      case StateOfLogin.NeedLogin: {
        Spreadsheet.getLogin({
          login: inputLogin,
        });
        setState(StateOfLogin.NeedCompany);
        break;
      }
      case StateOfLogin.NeedCompany: {
        //TODO
        setState(StateOfLogin.NeedPassword);
        break;
      }
      case StateOfLogin.NeedPassword: {
        //TODO
        setInfoMessage('Trwa logowanie...')

        setState(StateOfLogin.Loged);
        break;
      }
    }
  }

  let textInputCompany = null;
  if (state >= StateOfLogin.NeedCompany) {
    textInputCompany = (
      <TextInput
        style={styles.textInput}
        placeholder='Firma:'
        onChangeText={newText => setInputCompany(newText)}
        defaultValue={inputCompany}
      />
    );
  }
  let textInputPass = null;
  if (state >= StateOfLogin.NeedPassword) {
    textInputPass = (
      <TextInput
        style={styles.textInput}
        placeholder='Hasło:'
        onChangeText={newText => setInputPass(newText)}
        defaultValue={inputPass}
        secureTextEntry={true}
      />
    );
  }
  return (
    <View style={styles.container}>
      <TextInput
        style={styles.textInput}
        placeholder='Login:'
        onChangeText={newText => setInputLogin(newText)}
        defaultValue={inputLogin}
      />
      {textInputCompany}
      {textInputPass}
      <Text style={styles.text}>
        {infoMessage}
      </Text>
      <Button
        onPress={pressLogin}
        label='Zaloguj się'
        styleButton={{
          backgroundColor: '#363636',
          width: 200,
        }}
      />
    </View>
  );
}
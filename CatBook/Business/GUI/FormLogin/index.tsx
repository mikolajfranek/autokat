import React, { useState } from 'react';
import {
  TextInput,
  View,
} from 'react-native';
import styles from './style';
import Button from '../../../Backend/GUI/Buttons/MyBaseButtonViewTouchableHighlight';
import * as Spreadsheet from '../../Spreadsheet/Login';
import MyBaseText from '../../../Backend/GUI/Texts/MyBaseText';
import * as GlobalStyle from '../business_style';

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
      <MyBaseText>
        {infoMessage}
      </MyBaseText>
      <Button
        onPress={pressLogin}
        styleView={{
          width: 200,
        }}>
        <MyBaseText styleText={{ color: GlobalStyle.baseColorWhite, margin: 15 }}>
          Zaloguj się
        </MyBaseText>
      </Button>
    </View>
  );
}
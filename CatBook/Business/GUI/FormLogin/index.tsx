import React, { useState } from 'react';
import { TextInput, View } from 'react-native';
import styles from './style';
import Button from '../../../Backend/GUI/Buttons/MyBaseButtonViewTouchableHighlight';
import * as Spreadsheet from '../../Spreadsheet/Login';
import MyBaseText from '../../../Backend/GUI/Texts/MyBaseText';
import * as BusinessStyle from '../business_style';

enum FormLoginStatus {
  NeedLogin,
  NeedCompany,
  NeedPassword,
  Loged,
};

export default function render(): React.JSX.Element {
  const [status, setStatus] = useState(FormLoginStatus.NeedLogin);
  const [inputLogin, setInputLogin] = useState('');
  const [inputCompany, setInputCompany] = useState('');
  const [inputPass, setInputPass] = useState('');
  const [infoMessage, setInfoMessage] = useState('');

  function pressLogin() {
    switch (status) {
      case FormLoginStatus.NeedLogin: {
        //TODO
        Spreadsheet.getLogin({
          login: inputLogin,
        });
        setStatus(FormLoginStatus.NeedCompany);
        break;
      }
      case FormLoginStatus.NeedCompany: {
        //TODO
        setStatus(FormLoginStatus.NeedPassword);
        break;
      }
      case FormLoginStatus.NeedPassword: {
        //TODO
        setStatus(FormLoginStatus.Loged);
        break;
      }
    }
    if (status == FormLoginStatus.Loged) {
      //TODO
      setInfoMessage('Trwa logowanie...')
    }
  }
  let textInputCompany = null;
  if (status >= FormLoginStatus.NeedCompany) {
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
  if (status >= FormLoginStatus.NeedPassword) {
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
      <Button onPress={pressLogin}>
        <MyBaseText styleText={{ color: BusinessStyle.colorWhite, margin: 15 }}>
          Zaloguj się
        </MyBaseText>
      </Button>
    </View>
  );
}
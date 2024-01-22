import {
  StyleSheet,
} from 'react-native';
import * as GlobalStyle from './global_style';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: GlobalStyle.baseColorWhite,
  },
  welcome: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: GlobalStyle.baseFontSize,
    margin: 10,
    color: GlobalStyle.baseColorText,
  },
});

export default styles;
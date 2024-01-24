import { StyleSheet } from 'react-native';
import * as GlobalStyle from '../business_style';

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
});

export default styles;
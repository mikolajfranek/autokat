import { StyleSheet } from 'react-native';

import * as StyleBusiness from '../../style_business';

const styles = StyleSheet.create({
  container: {
    flex: 5,
  },
  form: {
    flex: 4,
    alignItems: 'center',
    justifyContent: 'center',
  },
  textInput: {
    borderWidth: 1,
    borderColor: StyleBusiness.colorDark,
    borderRadius: 15,
    padding: 2,
    height: 40,
    fontSize: 20,
    width: 300,
    margin: 10,
    textAlign: 'center',
    color: StyleBusiness.colorDark,
  },
  about: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default styles;
import { StyleSheet } from 'react-native';
import * as BusinessStyle from '../business_style';

const styles = StyleSheet.create({
    container: {
        flex: 4,
        alignItems: 'center',
        justifyContent: 'center',
    },
    textInput: {
        borderWidth: 1,
        borderColor: BusinessStyle.colorDark,
        borderRadius: 15,
        padding: 2,
        height: 40,
        fontSize: 20,
        margin: 10,
        textAlign: 'center',
        color: BusinessStyle.colorDark,
    },
});

export default styles;
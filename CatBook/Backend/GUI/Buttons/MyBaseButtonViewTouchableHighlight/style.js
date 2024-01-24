import { StyleSheet } from 'react-native';
import * as GlobalStyle from '../../backend_style';

const styles = StyleSheet.create({
    touchable: {
        borderRadius: 15,
    },
    button: {
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: GlobalStyle.baseColor,
        borderRadius: 15,
    },
});

export default styles;
import { StyleSheet } from "react-native";
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
    text: {
        textAlign: 'center',
        color: GlobalStyle.baseColorWhite,
        margin: 15,
        fontSize: GlobalStyle.baseFontSize,
    },
});

export default styles;
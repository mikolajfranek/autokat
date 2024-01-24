import { StyleSheet } from "react-native";
import * as GlobalStyle from '../business_style';

const styles = StyleSheet.create({
    container: {
        flex: 4,
        alignItems: 'center',
        justifyContent: 'center',
    },
    textInput: {
        borderWidth: 1,
        borderColor: GlobalStyle.baseColor,
        borderRadius: 15,
        padding: 2,
        height: 40,
        width: 300,
        fontSize: GlobalStyle.baseFontSize,
        margin: 10,
        textAlign: 'center',
        color: GlobalStyle.baseColorText,
    },
    text: {
        fontSize: (GlobalStyle.baseFontSize - 5),
        marginBottom: 10,
        color: GlobalStyle.baseColorText,
    }
});

export default styles;
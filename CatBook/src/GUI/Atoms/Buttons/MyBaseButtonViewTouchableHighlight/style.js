import { StyleSheet } from "react-native";
import * as BackendStyle from "../../../gui_style";

const styles = StyleSheet.create({
    touchable: {
        borderRadius: 15,
    },
    view: {
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: BackendStyle.colorPrimary,
        borderRadius: 15,
        paddingVertical: 10,
        paddingHorizontal: 25
    },
});

export default styles;
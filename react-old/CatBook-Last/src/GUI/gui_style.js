import { StyleSheet, Dimensions } from "react-native";
let { width } = Dimensions.get("window");
const scaleFactor = {
    _1of25: 1.0 / 25,
    _1of20: 1.0 / 20,
    _1of15: 1.0 / 15,
    _1of10: 1.0 / 10
};

export const styles = StyleSheet.create({
    baseFont: {
        fontSize: width * scaleFactor._1of25,
        color: colorText,
    },
    baseFontHeader: {
        fontSize: width * scaleFactor._1of15,
        color: colorText,
    },
});

//colors
export const colorPrimary = "#363636";
export const colorSecondary = "#1e3a8a";
export const colorText = "#363636";
export const colorTextWhite = "#cccccc";
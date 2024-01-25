import { StyleSheet, Dimensions } from 'react-native';
let { width } = Dimensions.get('window');
const scaleFactor = { Small: 1.0 / 20, Medium: 1.0 / 15, Large: 1.0 / 10 };

export const styles = StyleSheet.create({
    baseFont: {
        fontSize: width * scaleFactor.Small,
        color: colorText,
    },
    baseFontHeader: {
        fontSize: width * scaleFactor.Medium,
        color: colorText,
    },
});

//colors
export const colorPrimary = '#363636';
export const colorSecondary = '#1e3a8a';
export const colorText = '#363636';
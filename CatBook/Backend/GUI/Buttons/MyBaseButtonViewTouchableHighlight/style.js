import { StyleSheet } from 'react-native';
import * as BackendStyle from '../../backend_style';

const styles = StyleSheet.create({
    touchable: {
        borderRadius: 15,
    },
    view: {
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: BackendStyle.colorPrimary,
        borderRadius: 15,
    },
});

export default styles;
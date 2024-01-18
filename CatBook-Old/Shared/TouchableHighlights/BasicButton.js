import React, { useState } from 'react';
import {
    View,
    StyleSheet,
    Text,
    TouchableHighlight
} from 'react-native';

export default function render() {
    const [pressing, setPressing] = useState(false);

    function _onPressIn() {
        setPressing(true);
    }

    function _onPressOut() {
        setPressing(false);
    }

    return (
        <View style={styles.kontener}>
            <TouchableHighlight
                onPressIn={_onPressIn}
                onPressOut={_onPressOut}
                style={styles.dotyk}>
                <View style={styles.przycisk}>
                    <Text style={styles.witaj}>
                        {pressing ? 'Klik' : 'Naciśnij'}
                    </Text>
                </View>
            </TouchableHighlight>
        </View>
    );
}

const styles = StyleSheet.create({
    kontener: {
        flex: 1,
        alignItems: 'center',
    },
    przycisk: {
        backgroundColor: 'lightblue',
        borderRadius: 100,
        height: 100,
        width: 100,
        justifyContent: 'center',
        overflow: 'hidden',
    },
    dotyk: {
        borderRadius: 100
    },
    witaj: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
        color: '#ffffff',
    },
});
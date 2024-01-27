import React from 'react';
import { StyleSheet, View, Text } from "react-native";

export default function render({ catalyst }) {
    return (
        <View style={styles.container}>
            <View style={styles.row}>
                <Text>---{catalyst.id}---</Text>
            </View>
            <View style={styles.row}>
                <Text>---{catalyst.model}---</Text>
            </View>
            <View style={styles.row}>
                <Text>---{catalyst.brand}---</Text>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    row: {
        flex: 1,
    },
});
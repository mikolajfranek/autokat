import React from 'react'
import { StyleSheet, Text, View } from 'react-native'

export default function render({ glowne, opis, temp }) {
    return (
        <View style={styles.kontener}>
            <Text style={styles.duzyTekst}>{glowne}</Text>
            <Text style={styles.glownyTekst}>Bieżące warunki: {opis}</Text>
            <Text style={styles.duzyTekst}>{temp} C</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    kontener: { height: 130, },
    duzyTekst: {
        fontSize: 20,
        margin: 5,
        textAlign: 'center',
        color: '#FFFF00',
    },
    glownyTekst: { fontSize: 16, margin: 5, textAlign: 'center', color: '#FFFF00', }
});
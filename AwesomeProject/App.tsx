import React, { useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  TextInput,
  ImageBackground,
  Platform
} from 'react-native';
import Prognoza from './Prognoza.js';
import OpenWeatherMap from './open_weather_map.js'

export default function render() {
  const [kod, setKod] = useState('');
  const [prognoza, setPrognoza] = useState(null);

  function handleKodChange(e: { nativeEvent: { text: React.SetStateAction<string>; }; }) {
    let k = e.nativeEvent.text;
    OpenWeatherMap.pobierzPrognoze(k).then(item => setPrognoza(item));
    setKod(k);
  }

  return (
    <View style={styles.kontener}>
      <ImageBackground style={styles.tlo}
        source={
          (
            Platform.select({
              ios: require("./kwiaty.ios.png"),
              android: require("./kwiaty.android.png")
            })
          )
        }
        resizeMode='cover'>
        <View style={styles.nakladka}>
          <Text style={styles.witaj}>
            Wpisałeś {kod}
          </Text>
          {prognoza !== null
            ? <Prognoza
              glowne={prognoza.glowne}
              opis={prognoza.opis}
              temp={prognoza.temp}
            />
            : null}
          <TextInput
            style={styles.wejscie}
            onSubmitEditing={handleKodChange} />
        </View>
      </ImageBackground>
    </View>
  );
}

const styles = StyleSheet.create({
  kontener: { flex: 1 },
  tlo: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
  },
  nakladka: {
    backgroundColor: "#000000",
    opacity: 0.5,
    alignItems: "center",
    padding: 10,
  },
  witaj: {
    fontSize: 20,
    margin: 10,
    color: '#ffffff',
  },
  wejscie: {
    fontSize: 20,
    borderWidth: 1,
    borderColor: '#ffffff',
    padding: 2,
    height: 40,
    width: 200,
    textAlign: 'center',
    color: '#ffffff',
  },
});
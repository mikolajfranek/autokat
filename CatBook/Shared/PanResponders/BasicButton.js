import React, { useState, useEffect, useLayoutEffect } from 'react';
import { StyleSheet, PanResponder, View, Text } from "react-native";

var _panResponder = {};
var _poprzedniLewo = 0;
var _poprzedniGora = 0;
var _styleKol = {};
var kolo = null;

export default function render() {
    const [fetching, setFetching] = useState(false);
    const [mounted, setMounted] = useState(false);
    const [aktywneDotyki, setAktywneDotyki] = useState(0);
    const [dx, setDx] = useState(0);
    const [dy, setDy] = useState(0);
    const [vx, setVx] = useState(0);
    const [vy, setVy] = useState(0);

    async function sleep(miliseconds) {
        return new Promise(item => setTimeout(item, miliseconds));
    }

    //1. Your setup code runs when your component is added to the page (mounts).
    //2. After every re-render of your component where the dependencies have changed:
    //First, your cleanup code runs with the old props and state.
    //Then, your setup code runs with the new props and state.
    //3.Your cleanup code runs one final time after your component is removed from the page (unmounts).
    useEffect(() => {
        async function _startFetching() {
            console.log('start fetching...');
            await sleep(3000);
            console.log('end fetching...');
            //using promises does suffer from “race conditions”
            if (ignore === false) {
                setFetching(false); //aaa
            }
        }
        console.log('bbb');
        setMounted(true); //aaa
        _zaktualizujPozycje();
        let ignore = false;
        _startFetching();
        //componentDidMount
        return () => {
            console.log('ccc');
            ignore = true;
            //componentWillUnmount
        };
    },
        [fetching]//DependencyList
        //componentDidUpdate (componentWillUnmount && componentDidMount)
    )

    useLayoutEffect(() => {
        //The signature is identical to useEffect, 
        //but it fires synchronously after all DOM mutations. 
    },
        []
    );

    console.log('aaa');
    if (mounted === false) {
        _panResponder = PanResponder.create({
            onStartShouldSetPanResponder: _obslugaStartUstawPanResponder,
            onMoveShouldSetPanResponder: _obslugaRuchUstawPanResponder,
            onPanResponderGrant: _obslugaPanResponderPrzyznano,
            onPanResponderMove: _obslugaPanResponderRuch,
            onPanResponderRelease: _obslugaPanResponderKoniec,
            onPanResponderTerminate: _obslugaPanResponderKoniec
        });
        _poprzedniLewo = 0;
        _poprzedniGora = 0;
        _styleKol = {
            style: { left: _poprzedniLewo, top: _poprzedniGora }
        };
    }

    // _podswietl and _niePodswietl wywoływane są przez metody PanRespondera
    // zapewniając użytkownikowi informację graficzną.
    function _podswietl() {
        setFetching(true);
        kolo && kolo.setNativeProps({ style: { backgroundColor: KOLOR_PODSWIETLENIA_KOLA } });
    };

    function _niePodswietl() {
        kolo && kolo.setNativeProps({ style: { backgroundColor: KOLOR_KOLA } });
    };

    // Za pomocą setNativeProps kontrolujemy właściwości koła.
    function _zaktualizujPozycje() {
        kolo && kolo.setNativeProps(_styleKol);
    };

    function _obslugaStartUstawPanResponder(event, stanGestu) {
        // Czy obsługa zdarzeń powinna być aktywna, jeśli użytkownik nacisnął na koło?
        return true;
    };

    function _obslugaRuchUstawPanResponder(event, stanGestu) {
        // Czy obsługa zdarzeń powinna być aktywna, jeśli użytkownik przesunie palcem nad kołem?
        return true;
    };

    function _obslugaPanResponderPrzyznano(event, stanGestu) {
        _podswietl();
    };

    function _obslugaPanResponderRuch(event, stanGestu) {
        setAktywneDotyki(stanGestu.numberActiveTouches);
        setDx(stanGestu.dx);
        setDy(stanGestu.dy);
        setVx(stanGestu.vx);
        setVy(stanGestu.vy);
        // Obliczanie bieżącej pozycji przy pomocy delt
        _styleKol.style.left = _poprzedniLewo + stanGestu.dx;
        _styleKol.style.top = _poprzedniGora + stanGestu.dy;
        _zaktualizujPozycje();
    };

    function _obslugaPanResponderKoniec(event, stanGestu) {
        _niePodswietl();
        _poprzedniLewo += stanGestu.dx;
        _poprzedniGora += stanGestu.dy;
    };

    return (
        <View style={styles.container}>
            <View
                ref={item => { kolo = item; }}
                style={styles.kolo}
                {..._panResponder.panHandlers}
            />
            <Text style={styles.witaj}>{fetching ? 'Fetching max 3s...' : ''}</Text>
            <Text style={styles.witaj}>{aktywneDotyki} dotyków, dx: {dx}, dy: {dy}, vx: {vx}, vy: {vy}</Text>
        </View>
    );
}

const ROZMIAR_KOLA = 50;
const KOLOR_KOLA = "lightgray";
const KOLOR_PODSWIETLENIA_KOLA = "green";

const styles = StyleSheet.create({
    kolo: {
        width: ROZMIAR_KOLA,
        height: ROZMIAR_KOLA,
        borderRadius: ROZMIAR_KOLA,
        backgroundColor: KOLOR_KOLA,
        position: "absolute",
        left: 0,
        top: 0,
    },
    container: {
        flex: 1,
        position: "absolute",
    },
});
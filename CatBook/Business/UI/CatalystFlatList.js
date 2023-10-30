import React, { useState } from 'react';
import { StyleSheet, FlatList } from "react-native";
import UICatalyst from './Catalyst';
import ModelCatalyst from './../Models/Catalyst';

function _renderItem({ item }) {
    return <UICatalyst catalyst={item.uicontent} />
}

function _downloadList() {
    const cat1 = new ModelCatalyst(1,
        'kat 1',
        'Ford',
        'Ceramiczny',
        1.2,
        0.1,
        4.12,
        0.3,
        'AUDI5574',
        'https://drive.google.com/file/d/0B07utEGHtmwbMXRRNFF3RThDQW9oZmJhVGhPenpXdEJveHFV/view?usp=sharing&resourcekey=0-_ekHGwCWh-DPA33jGHxLgw',
        null
    );
    const cat2 = new ModelCatalyst(2,
        'kat 2',
        'Nissan',
        'Metalowy',
        4.32,
        0.32,
        4.12,
        0.35,
        'AUDI5575',
        'https://drive.google.com/file/d/0B07utEGHtmwbMXRRNFF3RThDQW9oZmJhVGhPenpXdEJveHFV/view?usp=sharing&resourcekey=0-_ekHGwCWh-DPA33jGHxLgw',
        null
    );
    const cat3 = new ModelCatalyst(3,
        'kat 3',
        'Lexus',
        'Ceramiczny',
        4.1,
        0.3,
        0.22,
        0.1,
        'AUDI5576',
        'https://drive.google.com/file/d/0B07utEGHtmwbMXRRNFF3RThDQW9oZmJhVGhPenpXdEJveHFV/view?usp=sharing&resourcekey=0-_ekHGwCWh-DPA33jGHxLgw',
        null
    );
    const list = [
        { key: 1, uicontent: cat1 },
        { key: 2, uicontent: cat2 },
        { key: 3, uicontent: cat3 }
    ];
    return list;
}

export default function render() {
    var list = _downloadList();
    return (
        <FlatList
            data={list}
            renderItem={_renderItem} />
    );
}

const styles = StyleSheet.create({
    container: { flex: 1 },
});
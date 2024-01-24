import React, { PropsWithChildren } from 'react';
import { StyleProp, TextStyle } from 'react-native';
import styles from './style';
import MyBaseText from '../MyBaseText';

type MyHeaderTextProps = {
    styleText?: StyleProp<TextStyle>;
};

export default function render(props: PropsWithChildren<MyHeaderTextProps>) {
    return (
        <MyBaseText styleText={[styles.text, props.styleText]}>
            {props.children}
        </MyBaseText>
    );
}
import React from 'react';
import { Text, View } from 'react-native';
import * as StyleBusiness from '../../style_business';

export default function App(): React.JSX.Element {
    return (
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: StyleBusiness.colorWhite }}>
            <Text>
                Katalizatory
            </Text>
        </View>
    );
}
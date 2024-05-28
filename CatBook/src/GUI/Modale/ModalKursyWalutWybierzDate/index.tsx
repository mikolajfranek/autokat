import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { Button, Text, View } from 'react-native';
import { LocalRealmContext } from '../../../Database/LocalRealmContext';
import CourseExchange from '../../../Database/Models/CourseExchange';

export default function App(): React.JSX.Element {
    const navigation = useNavigation();
    /**
     * 
     * #lista jako modal 
     * sortowanie po desc
     * filtrowanie po dacie
     * oznaczenie aktualnego wykorzystywanego kursu..
     */

    const { useQuery } = LocalRealmContext;
    const items = useQuery(CourseExchange)
        .sorted('_created_at', true);

    return (
        <View style={{ flex: 2 }}>
            <View
                style={{ flex: 1, justifyContent: 'center' }}>
                <Text style={{ alignSelf: 'center' }} >
                    Hello CatBook ModalTmp
                </Text>
            </View>
            <View
                style={{ flex: 1, width: 100, alignSelf: 'center' }}>
                <Button
                    onPress={() => navigation.navigate({
                        name: 'Screen_MaterialBottomTab_KursyWalut',
                        params: { id: 'bbb' },
                        merge: true
                    })}
                    title="OK" />
            </View>
            {items.map(item => (
                <View style={{ marginTop: 10 }}>
                    <Text>{item._id.toString()}</Text>
                    <Text>{item._value_eur_mid.toString()}</Text>
                    <Text>{item._value_usd_mid.toString()}</Text>
                    <Text>{item._effectived_at.toString()}</Text>
                </View>
            ))}
        </View>
    );
}
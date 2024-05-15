import React from 'react';
import { View } from 'react-native';
import { Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseExchange from '../../Database/Models/CourseExchange';

export default function App(): React.JSX.Element {
    /**
     * problem, ze eur i usd mam w osobnych wierszach?
     * 
     * #aktualny widok
     * aktualnie wykorzystywane kursy
     * przycisk przejdź do listy
     * 
     * #lista
     * sortowanie po desc
     * przycisk pobierz kursy
     * filtrowanie po dacie?
     * oznaczenie aktualnego wykorzystywanego kursu..
     * 
     * 
     */

    const { useQuery } = LocalRealmContext;
    const items = useQuery(CourseExchange)
        .sorted('_created_at', true);
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello KursyWalut
            </Text>
            {items.map(item => (
                <View style={{ marginTop: 10 }}>
                    <Text>{item._id.toString()}</Text>
                    <Text>{item._value_eur_mid.toString()}</Text>
                    <Text>{item._eur_effectived_at.toString()}</Text>
                    <Text>{item._value_usd_mid.toString()}</Text>
                    <Text>{item._usd_effectived_at.toString()}</Text>
                </View>
            ))}
        </View>
    );
}
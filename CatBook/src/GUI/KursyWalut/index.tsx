import React from 'react';
import { View } from 'react-native';
import { Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseExchange from '../../Database/Models/CourseExchange';
import { Currency } from '../../Enums/Currency';

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

    const sortedProfiles = useQuery(CourseExchange)
        //.filtered("type == $0", Currency.eur)
        .sorted('_created_at', true);

    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello KursyWalut
            </Text>
            {sortedProfiles.map(item => (
                <Text>{item._id.toString()} {item._created_at.toString()} {item._type} {item._value_mid.toString()} {item._effectived_at.toString()}</Text>
            ))}
        </View>
    );
}
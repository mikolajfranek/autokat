import { useNavigation } from '@react-navigation/native';
import React, { useState } from 'react';
import { Button, Text, View } from 'react-native';
import { LocalRealmContext } from '../../../Database/LocalRealmContext';
import CourseExchange from '../../../Database/Models/CourseExchange';
import { TextInput } from 'react-native-paper';

export default function App(): React.JSX.Element {
    const navigation = useNavigation();
    const [filtr, setFiltr] = useState('');
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
        <View style={{ flex: 2, justifyContent: 'center' }}>
            <View style={{ margin: 10 }}>
                {/* TODO maska yyyy-mm-dd */}
                <TextInput
                    placeholder='YYYY-MM-DD'
                    onChangeText={newText => setFiltr(newText)}
                />
            </View>
            <View
                style={{ flex: 2, width: 100, alignSelf: 'center' }}>
                <Button
                    onPress={() => navigation.navigate({
                        name: 'Screen_MaterialBottomTab_KursyWalut',
                        params: { id: 'bbb' },
                        merge: true
                    })}
                    title="OK" />
            </View>
            {items.map(item => item.getJSX())}
        </View>
    );
}
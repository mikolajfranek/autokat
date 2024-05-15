import React from 'react';
import { View } from 'react-native';
import { Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseMetal from '../../Database/Models/CourseMetal';

export default function App(): React.JSX.Element {
    const { useQuery } = LocalRealmContext;
    const items = useQuery(CourseMetal)
        .sorted('_created_at', true);
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello KursyMetali
            </Text>
            {items.map(item => (
                <View style={{ marginTop: 10 }}>
                    <Text>{item._id.toString()}</Text>
                    <Text>{item._created_at.toString()}</Text>
                    <Text>{item._platinum_bid.toString()}</Text>
                    <Text>{item._palladium_bid.toString()}</Text>
                    <Text>{item._rhodium_bid.toString()}</Text>
                    <Text>{item._effectived_at.toString()}</Text>
                </View>
            ))}
        </View>
    );
}
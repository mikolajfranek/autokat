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

/*

            <Button
                icon='download'
                onPress={async () => {
                    try {
                        const data_kursu = new Date();
                        const metal = await getMetal({ data_kursu: data_kursu }).unwrap();
                        const platinum_bid = metal.data.platinum.results[0].bid;
                        const palladium_bid = metal.data.palladium.results[0].bid;
                        const rhodium_bid = metal.data.rhodium.results[0].bid;
                        console.log(data_kursu);
                        realm.write(() => {
                            realm.create<CourseMetal>(CourseMetal, {
                                _id: new BSON.ObjectId(),
                                _platinum_bid: platinum_bid,
                                _palladium_bid: palladium_bid,
                                _rhodium_bid: rhodium_bid,
                                _effectived_at: data_kursu
                            });
                        });
                        //zapisz jeśli ...
                    } catch (error) {
                        console.error(error)
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Pobierz kursy metali
            </Button>
*/
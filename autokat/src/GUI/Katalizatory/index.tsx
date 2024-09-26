import React from 'react';
import { Alert,  View } from 'react-native';
import { Button, Divider, Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import Catalyst from '../../Database/Models/Catalyst';
import { useGetCatalystCountMutation, useGetCatalystMutation } from '../../APIGoogle/APIDocs';
import { BSON } from 'realm';

export default function App(): React.JSX.Element {
    const { useQuery, useRealm } = LocalRealmContext;
    const realm = useRealm();

    const itemsAll = useQuery(Catalyst);
    const items = itemsAll
        .sorted('_created_at', true);

    const [getCatalyst] = useGetCatalystMutation();
    const [getCatalystCount] = useGetCatalystCountMutation();
        
 
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
             <Button
                icon='sync'
                onPress={async () => {
                    try {


                        const catalysts = await getCatalyst({ fromId:0 }).unwrap();
                        
                        catalysts.table.rows.forEach(item => {
                            realm.write(() => {
                                realm.create(Catalyst, {
                                    _id: new BSON.ObjectId(),
                                    _ident: parseInt(item.c[0].v),
                                    _type: item.c[7].v == "Ceramiczny" ? 1 : 0,
                                    _name: item.c[1].v,
                                    _brand: item.c[2].v,
                                    _picture_id: item.c[8].v,
                                    _weight: parseFloat(item.c[3].v),
                                    _platinum: parseFloat(item.c[4].v),
                                    _palladium: parseFloat(item.c[5].v),
                                    _rhodium: parseFloat(item.c[6].v)
                                });
                            });
                        });


                  


                        //todo...
                    } catch (error) {
                        console.log(error);
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (pełna)
            </Button>
            <Button
                icon='sync'
                onPress={async () => {
                    try {
                        realm.write(() => {
                            realm.delete(itemsAll);
                        });
                    } catch (error) {
                        console.log(error);
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (przyrostowa)
            </Button>
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            
            <Text style={{ alignSelf: 'center' }} >
                Hello Katalizatory
            </Text>
            {items.map(item => (
                <View style={{ marginTop: 2 }}>
                    <Text>{item._ident.toString()} - {item._created_at.toString()} {item._picture_id.toString()}</Text>
                </View>
            ))}
        </View>
    );
}
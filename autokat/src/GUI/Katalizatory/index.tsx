import React, { useState } from 'react';
import { Alert, PressableAndroidRippleConfig, StyleProp, TextStyle, View, ViewStyle, useWindowDimensions } from 'react-native';
import { Button, Divider, Text, Searchbar, Icon } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import Catalyst from '../../Database/Models/Catalyst';
import { useGetCatalystCountMutation, useGetCatalystMutation } from '../../APIGoogle/APIDocs';
import { BSON } from 'realm';

import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
const Tab = createMaterialTopTabNavigator();


const FirstRoute = () => {
    const { useQuery } = LocalRealmContext;
    const itemsAll = useQuery(Catalyst);
    const items = itemsAll
        .sorted('_created_at', true);

    const [filter, setFilter] = useState('');
    return (
        <View style={{ flex: 1 }}>
            <Searchbar
                style={{ marginTop: 10 }}
                placeholder="Wyszukaj..."
                onChangeText={setFilter}
                value={filter}
            />

            <Icon
                source="content-save"
                size={30}
            />

            <Icon
                source="playlist-edit"
                size={30}
            />


            {items.map(item => (
                <View style={{ marginTop: 2 }}>
                    <Text>{item._ident.toString()} - {item._created_at.toString()} {item._picture_id.toString()}</Text>
                </View>
            ))}
        </View>
    )
};

const SecondRoute = () => {

    const { useQuery, useRealm } = LocalRealmContext;
    const realm = useRealm();

    const itemsAll = useQuery(Catalyst);
    const items = itemsAll
        .sorted('_created_at', true);

    const [getCatalyst] = useGetCatalystMutation();
    const [getCatalystCount] = useGetCatalystCountMutation();
    return (
        <View style={{ flex: 1 }} >
            <Button
                icon='sync'
                onPress={async () => {
                    try {
                        realm.write(() => {
                            realm.delete(itemsAll);
                        });

                        const catalysts = await getCatalyst({ fromId: 0 }).unwrap();

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

                    } catch (error) {
                        console.log(error);
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (przyrostowa)
            </Button>
        </View>
    );
}


export default function App(): React.JSX.Element {

    return (
        <Tab.Navigator>
            <Tab.Screen name="Screen_MaterialBottomTab_Katalizatory_Home" component={FirstRoute} 
              options={{
                tabBarShowLabel: false,
                tabBarIcon: ({ color }) => (
                    <MaterialCommunityIcons name="home" color={color} size={26} />
                ),
            }}
            />
            <Tab.Screen name="Screen_MaterialBottomTab_Katalizatory_Settings" component={SecondRoute} 
                options={{
                tabBarShowLabel: false,
                tabBarIcon: ({ color }) => (
                    <MaterialCommunityIcons name="cog" color={color} size={26} />
                ),
            }}
            />
        </Tab.Navigator>
    );
}
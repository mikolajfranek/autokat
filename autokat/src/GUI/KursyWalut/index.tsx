import React, { useState } from 'react';
import { Alert, Switch, View } from 'react-native';
import { Button, Divider, Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseExchange from '../../Database/Models/CourseExchange';
import { useGetExchangeMutation } from '../../APIExchange';
import { Currency } from '../../Enums/Currency';
import { BSON } from 'realm';
import { deleteLocalStorage, getLocalStorageStringOrUndefined } from '../../LocalStorage';
import { LocalStorageKeys } from '../../Enums/LocalStorageKeys';
import { useNavigation, useRoute } from '@react-navigation/native';
import Toast from 'react-native-toast-message';

export default function App(): React.JSX.Element {
    const navigation = useNavigation();
    const route = useRoute();
    const { useRealm } = LocalRealmContext;
    const realm = useRealm();
    const [getExchange] = useGetExchangeMutation();
    const newestExchangeID = getLocalStorageStringOrUndefined(LocalStorageKeys.newestExchangeID);
    const [isNewestExchange, setIsNewestExchange] = useState(newestExchangeID === undefined);


    return (
        <View style={{ alignItems: 'center', margin: 15 }}>
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>Data pobrania:  </Text>
                <Text>(data kursu)  </Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>Kurs EUR:  </Text>
                <Text>(data kursu)  </Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Kurs USD:  </Text>
                <Text>(data kursu)  </Text>
            </View>

            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />

            <Button
                icon='download'
                onPress={async () => {
                    try {
                        const eur = await getExchange({ currency: Currency.eur }).unwrap();
                        const usd = await getExchange({ currency: Currency.usd }).unwrap();
                        const eur_effective_date = new Date(eur.rates[0].effectiveDate);
                        const eur_mid = eur.rates[0].mid;
                        const usd_effective_date = new Date(usd.rates[0].effectiveDate);
                        const usd_mid = usd.rates[0].mid;
                        realm.write(() => {
                            realm.create<CourseExchange>(CourseExchange, {
                                _id: new BSON.ObjectId(),
                                _eur_effectived_at: eur_effective_date,
                                _eur_mid: eur_mid,
                                _usd_effectived_at: usd_effective_date,
                                _usd_mid: usd_mid
                            });
                        });

                        Toast.show({
                            type: 'success',
                            text1: 'Pomyślnie pobrano kursy walut',
                            position: 'bottom'
                          });
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Pobierz kursy walut
            </Button>

            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />

            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Najnowsze kursy walut</Text>
                <Switch
                    style={{ marginLeft: 15 }}
                    value={isNewestExchange}
                    onValueChange={(newValue) => {
                        if (newValue) {
                            deleteLocalStorage(LocalStorageKeys.newestExchangeID);
                            setIsNewestExchange(true);
                            //powinienem pobrać kursy ???
                        } else {
                            //jesli jest false przejdz od razu do listy (jako modal), zeby wybrac date
                            navigation.navigate('Stack.Screen_ModalKursyWalutWybierzDate' as never);
                            //wartość zwrotna i zapisanie, odświeżenie widoku
                        }
                    }}
                />
            </View>

            {
                isNewestExchange == false &&
                <Button
                    icon='calendar-search'
                    onPress={() => navigation.navigate('Stack.Screen_ModalKursyWalutWybierzDate' as never)}>
                    Wybierz inną datę
                </Button>
            }


            <Text>
                {(route.params as any)?.id}
            </Text>
        </View>
    );
}

/*
    //const _id = BSON.ObjectId.createFromHexString("664515e8df2cc69c2fab06c7");
    //const myTask = useObject<CourseExchange>(CourseExchange, _id);
    //var current = realm._objectForObjectKey<CourseExchange>(CourseExchange, _id);



    const [getMetal] = useGetMetalsMutation();
*/
import React from 'react';
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

export default function App(): React.JSX.Element {
    const navigation = useNavigation();
    const route = useRoute();

    const { useRealm } = LocalRealmContext;
    const realm = useRealm();
    const [getExchange] = useGetExchangeMutation();
    const newestExchangeID = getLocalStorageStringOrUndefined(LocalStorageKeys.newestExchangeID);
    const isNewestExchange = newestExchangeID === undefined;
    return (
        <View style={{ alignItems: 'center', margin: 15 }}>
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>Data kursów:</Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>Kurs EUR:  </Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Kurs USD:  </Text>
            </View>

            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />

            <Button
                icon='download'
                onPress={async () => {
                    try {
                        const eur = await getExchange({ currency: Currency.eur }).unwrap();
                        const usd = await getExchange({ currency: Currency.usd }).unwrap();
                        const eur_mid = eur.rates[0].mid;
                        const eur_effective_date = new Date(eur.rates[0].effectiveDate);
                        const usd_mid = usd.rates[0].mid;
                        const usd_effective_date = new Date(usd.rates[0].effectiveDate);
                        const array_of_dates = [eur_effective_date.getTime(), usd_effective_date.getTime()];
                        const effective_date = new Date(Math.max(...array_of_dates));
                        console.log(effective_date);
                        realm.write(() => {
                            realm.create<CourseExchange>(CourseExchange, {
                                _id: new BSON.ObjectId(),
                                _value_eur_mid: eur_mid,
                                _value_usd_mid: usd_mid,
                                _effectived_at: effective_date,
                            });
                        });

                    } catch (error) {
                        console.log(error);
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
                        } else {
                            //jesli jest false przejdz od razu do listy (jako modal), zeby wybrac date
                        }
                    }}
                />
            </View>

            <Button
                icon='calendar-search'
                onPress={() => navigation.navigate('Stack.Screen_ModalKursyWalutWybierzDate')}>
                Wybierz inną datę
            </Button>
            <Text>

                {route.params?.id}
            </Text>
        </View>
    );
}
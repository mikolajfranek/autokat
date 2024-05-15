import React from 'react';
import { Alert, Linking, View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Divider, Icon, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';
import { useGetExchangeMutation } from '../../APIExchange';
import { Currency } from '../../Enums/Currency';
import { useGetMetalsMutation } from '../../APIMetal';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseExchange from '../../Database/Models/CourseExchange';
import { BSON, Types } from 'realm';
import CourseMetal from '../../Database/Models/CourseMetal';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    const { useRealm } = LocalRealmContext;
    const realm = useRealm();



    //const _id = BSON.ObjectId.createFromHexString("664515e8df2cc69c2fab06c7");
    //const myTask = useObject<CourseExchange>(CourseExchange, _id);
    //var current = realm._objectForObjectKey<CourseExchange>(CourseExchange, _id);



    const [getExchange] = useGetExchangeMutation();
    const [getMetal] = useGetMetalsMutation();
    return (
        <View style={{ alignItems: 'center', margin: 15 }}>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Icon source='account-tie' size={48} />
                <Text> Sławomir Szukała</Text>
            </View>
            <Button
                icon='mail'
                onPress={() => Linking.openURL('mailto:autokat.katalizatory@gmail.com')}>
                autokat.katalizatory@gmail.com
            </Button>
            <Button
                icon='phone'
                onPress={() => Linking.openURL('tel:+48510076585')}>
                510 076 585
            </Button>
            <Button
                icon='web'
                onPress={() => Linking.openURL('https:katalizatorypoznan.pl')}>
                katalizatorypoznan.pl
            </Button>
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text
                    style={{ verticalAlign: 'middle' }}>Ciemny layout</Text>
                <Switch
                    style={{ marginLeft: 15 }}
                    value={isThemeDark}
                    onValueChange={toggleTheme}
                />
            </View>
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            <Button
                icon='sync'
                onPress={async () => {
                    try {
                        //todo...
                    } catch (error) {
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
                        //todo...
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (przyrostowa)
            </Button>

            <Button
                icon='download'
                onPress={async () => {
                    try {
                        const eur = await getExchange({ currency: Currency.eur }).unwrap();
                        const usd = await getExchange({ currency: Currency.usd }).unwrap();
                        const eur_mid = eur.rates[0].mid;
                        const eur_effectiveDate = eur.rates[0].effectiveDate;
                        const usd_mid = usd.rates[0].mid;
                        const usd_effectiveDate = usd.rates[0].effectiveDate;
                        realm.write(() => {
                            realm.create<CourseExchange>(CourseExchange, {
                                _id: new BSON.ObjectId(),
                                _value_eur_mid: eur_mid,
                                _value_usd_mid: usd_mid,
                                _eur_effectived_at: eur_effectiveDate,
                                _usd_effectived_at: usd_effectiveDate
                            });
                        });
                        //zapisz jeśli ...
                    } catch (error) {
                        console.log(error);
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Pobierz kursy walut
            </Button>
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
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            <Button
                icon='logout'
                onPress={() => dispatch(setAuthenticated(false))}>
                Wyloguj się
            </Button>
        </View>
    );
}
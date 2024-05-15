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

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    const { useQuery, useRealm } = LocalRealmContext;
    const realm = useRealm();

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
            <View style={{ marginLeft: 0, flexDirection: 'row', alignItems: 'center' }}>
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
                        console.log('---');
                        realm.write(() => {
                            realm.create<CourseExchange>(CourseExchange, {
                                _id: new BSON.ObjectId(),
                                _type: Currency.eur,
                                _value_mid: Types.Decimal128.fromString(eur.rates[0].mid),
                                _effectived_at: eur.rates[0].effectiveDate
                            });
                        });

                        console.log(eur.rates[0].mid);
                        console.log(eur.rates[0].effectiveDate);


                        //const usd = await getExchange({ currency: Currency.usd }).unwrap();



                        //useRealm


                        //effectiveDate

                        // const sortedProfiles = useQuery(CourseExchange)
                        //     //.filtered("type == $0", Currency.eur)
                        //     .sorted('_id', true);
                        // for (var item in sortedProfiles) {
                        //     console.log(item);
                        // }

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
                        const metal = await getMetal().unwrap();
                        const platinum = metal.data.platinum.results[0].bid;
                        const palladium = metal.data.palladium.results[0].bid;
                        const rhodium = metal.data.rhodium.results[0].bid;

                        //this working
                        // await database.write(async () => {
                        //     const newItem = await database.get('courses_exchange').create(item => {
                        //         item.platinum = platinum;
                        //         item.palladium = palladium;
                        //         item.rhodium = rhodium;
                        //     });
                        // });

                        //https://www.mongodb.com/docs/atlas/device-sdks/sdk/react-native/install/#std-label-react-native-install


                        //collection.create
                        //const courses = database.collections.get("courses_exchange") as CourseMetal;
                        //courses.add(platinum, palladium, rhodium);

                        // const starredPosts = await postsCollection.query(Q.where('is_starred', true)).fetch()


                        //const numberOfStarredPosts = await database.get('courses_exchange')
                        //.query().fetchCount();

                        //console.log(numberOfStarredPosts);


                        //console.log(item);
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
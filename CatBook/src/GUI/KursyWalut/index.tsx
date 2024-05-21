import React from 'react';
import { Alert, Switch, View } from 'react-native';
import { Button, Divider, Text } from 'react-native-paper';
import { LocalRealmContext } from '../../Database/LocalRealmContext';
import CourseExchange from '../../Database/Models/CourseExchange';
import { PreferencesContext } from '../../PreferencesContext';
import { useGetExchangeMutation } from '../../APIExchange';
import { Currency } from '../../Enums/Currency';
import { BSON } from 'realm';

export default function App(): React.JSX.Element {
    /**
     * 
     * #lista jako modal (gdy switch jest false)
     * sortowanie po desc
     * filtrowanie po dacie
     * oznaczenie aktualnego wykorzystywanego kursu..
     */

    const { useRealm, useQuery } = LocalRealmContext;
    const items = useQuery(CourseExchange)
        .sorted('_created_at', true);
    const realm = useRealm();
    const [getExchange] = useGetExchangeMutation();


    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    return (
        <View style={{ alignItems: 'center', margin: 15 }}>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Aktualnie:</Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Data kursów:</Text>
            </View>
            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
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
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />

            <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                <Text>Najnowsze kursy walut</Text>
                <Switch
                    style={{ marginLeft: 15 }}
                    value={isThemeDark}
                    onValueChange={() => {
                        //skasuj aktualnie wybrany
                        //jesli jest true to pobierz ostatni element z bazy danych i przypisz do aktualnego
                        //jesli jest false przejdz od razu do listy (jako modal), zeby wybrac date
                    }}
                />
            </View>

            <Button
                icon='calendar-search'
                onPress={async () => {
                    try {
                        //
                    } catch (error) {
                        console.log(error);
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Wybierz inną datę
            </Button>
            {items.map(item => (
                <View style={{ marginTop: 10 }}>
                    <Text>{item._id.toString()}</Text>
                    <Text>{item._value_eur_mid.toString()}</Text>
                    <Text>{item._value_usd_mid.toString()}</Text>
                    <Text>{item._effectived_at.toString()}</Text>
                </View>
            ))}
        </View>
    );
}
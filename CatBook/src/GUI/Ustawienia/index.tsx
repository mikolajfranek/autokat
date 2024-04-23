import React from 'react';
import { Alert, Linking, View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Divider, Icon, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';
import { useGetExchangeMutation } from '../../APIExchange';
import { Currency } from '../../Enums/Currency';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    const [getExchange] = useGetExchangeMutation();
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
                        const usd = await getExchange({ currency: Currency.usd }).unwrap();

                        //effectiveDate
                        console.log(eur.rates[0].mid);
                        console.log(eur.rates[0].effectiveDate);

                        //var item = await CourseExchange.add(eur, usd);
                        //console.log(item);

                    } catch (error) {
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
                        //TODO
                    } catch (error) {
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
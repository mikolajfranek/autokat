import React from 'react';
import { Alert, View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';
import { useGetExchangeMutation } from '../../APIExchange';
import CourseExchange from '../../Database/Models/CourseExchange';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    const [getExchange] = useGetExchangeMutation();

    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <View style={{ flexDirection: 'row', justifyContent: 'center' }}>
                <Text>Ciemny layout</Text>
                <Switch
                    style={{ alignSelf: 'center' }}
                    color={'red'}
                    value={isThemeDark}
                    onValueChange={toggleTheme}
                />
            </View>
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
                Synchronizuj bazę danych
            </Button>
            <Button
                icon='exchange'
                onPress={async () => {
                    try {
                        const eur = await getExchange({currency: 'eur'}).unwrap();
                        const usd = await getExchange({currency: 'usd'}).unwrap();

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
                Synchronizuj kursy walut
            </Button>
            <Button
                icon='license'
                onPress={async () => {
                    try {
                        //todo...
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Licencje
            </Button>
            <Button
                icon='about'
                onPress={async () => {
                    try {
                        //todo...
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                O programie
            </Button>
            <Button
                icon='logout'
                onPress={async () => {
                    dispatch(setAuthenticated(false))
                }}>
                Wyloguj się
            </Button>
        </View>
    );
}
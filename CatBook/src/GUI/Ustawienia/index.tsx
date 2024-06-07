import React from 'react';
import { Linking, View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Divider, Icon, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
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
                <Text>Ciemny layout</Text>
                <Switch
                    style={{ marginLeft: 15 }}
                    value={isThemeDark}
                    onValueChange={toggleTheme}
                />
            </View>
            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            <Button
                icon='logout'
                onPress={() => dispatch(setAuthenticated(false))}>
                Wyloguj się
            </Button>
        </View>
    );
}
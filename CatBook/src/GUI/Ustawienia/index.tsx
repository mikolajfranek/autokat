import React from 'react';
import { View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <View style={{flexDirection: 'row', justifyContent: 'center'}}>
                <Text>Ciemny layout</Text>
                <Switch
                    style={{ alignSelf: 'center' }}
                    color={'red'}
                    value={isThemeDark}
                    onValueChange={toggleTheme}
                />
            </View>
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
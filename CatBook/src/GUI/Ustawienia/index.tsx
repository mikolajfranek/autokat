import React from 'react';
import { Image, View } from 'react-native';
import { setAuthenticated } from '../../Slices/Auth';
import { useAppDispatch } from '../../hooks';
import { Button, Switch, Text } from 'react-native-paper';
import { PreferencesContext } from '../../PreferencesContext';

export default function App(): React.JSX.Element {
    const dispatch = useAppDispatch();
    const { toggleTheme, isThemeDark } = React.useContext(PreferencesContext);
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Switch
                style={{ alignSelf: 'center' }}
                color={'red'}
                value={isThemeDark}
                onValueChange={toggleTheme}
            />

            <Text style={{ alignSelf: 'center' }} >
                Hello CatBook Ustawienia
            </Text>

            <Button icon={require('./chameleon.jpg')}
                theme={{ colors: { primary: undefined } }} >
                Is black text
                <Text>Success too</Text>
            </Button>

            <Button
                icon={() => (
                    <Image
                        source={require('./chameleon.jpg')}
                        style={{ width: 100, height: 100 }}
                    />
                )}
            >
                Success
            </Button>
            <Button
                icon='logout'
                onPress={async () => {
                    //TODO
                    dispatch(setAuthenticated(false))
                }}>
                Wyloguj się
            </Button>
        </View>
    );
}
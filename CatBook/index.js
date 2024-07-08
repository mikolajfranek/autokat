import 'react-native-gesture-handler'; //https://reactnavigation.org/docs/stack-navigator/
import 'react-native-get-random-values'; //https://github.com/LinusU/react-native-get-random-values
import { adaptNavigationTheme, MD3DarkTheme, MD3LightTheme, PaperProvider } from 'react-native-paper';
import { DefaultTheme as NavigationDefaultTheme, DarkTheme as NavigationDarkTheme, NavigationContainer } from '@react-navigation/native';
import { useState, useCallback, useMemo } from 'react';
import { getLocalStorageBoolean, setLocalStorage } from './src/LocalStorage';
import { LocalStorageKeys } from './src/Enums/LocalStorageKeys';
import { Provider } from 'react-redux';
import { store } from './src/store';
import { LocalRealmContext } from './src/Database/LocalRealmContext';
import { PreferencesContext } from './src/PreferencesContext';
import App from './src';
import Toast from 'react-native-toast-message';
import { AppRegistry } from 'react-native';
import { name } from './app.json';

const { LightTheme, DarkTheme } = adaptNavigationTheme({
    reactNavigationLight: NavigationDefaultTheme,
    reactNavigationDark: NavigationDarkTheme
});

const CombinedDarkTheme = {
    ...MD3DarkTheme,
    ...DarkTheme,
    colors: {
        ...MD3DarkTheme.colors,
        ...DarkTheme.colors
    }
};

const CombinedDefaultTheme = {
    ...MD3LightTheme,
    ...LightTheme,
    colors: {
        ...MD3LightTheme.colors,
        ...LightTheme.colors
    }
};

function getComponent() {
    const [isThemeDark, setIsThemeDark] = useState(getLocalStorageBoolean(LocalStorageKeys.isThemeDark));
    const currentTheme = isThemeDark ? CombinedDarkTheme : CombinedDefaultTheme;
    const toggleTheme = useCallback(() => {
        const value = !isThemeDark;
        setLocalStorage(LocalStorageKeys.isThemeDark, value);
        setIsThemeDark(value);
    }, [isThemeDark]);
    const preferences = useMemo(
        () => ({
            toggleTheme,
            isThemeDark
        }),
        [toggleTheme, isThemeDark]
    );
    const { RealmProvider } = LocalRealmContext;
    return (
        <>
            <Provider store={store}>
                <PreferencesContext.Provider value={preferences}>
                    <PaperProvider theme={currentTheme}>
                        <NavigationContainer theme={currentTheme}>
                            <RealmProvider>
                                <App />
                            </RealmProvider>
                        </NavigationContainer>
                    </PaperProvider>
                </PreferencesContext.Provider>
            </Provider>
            <Toast />
        </>
    );
}

AppRegistry.registerComponent(name, () => getComponent);
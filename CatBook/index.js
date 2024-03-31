import 'react-native-gesture-handler'; //https://reactnavigation.org/docs/stack-navigator/
import { AppRegistry } from 'react-native';
import { name } from './app.json';
import { Provider } from 'react-redux';
import { store } from './src/store';
import App from './src';
import { NavigationContainer, DarkTheme as NavigationDarkTheme, DefaultTheme as NavigationDefaultTheme } from '@react-navigation/native';
import { adaptNavigationTheme, MD3LightTheme, MD3DarkTheme, PaperProvider } from 'react-native-paper';
import { useState, useCallback, useMemo } from 'react';
import { getLocalStorageBoolean, setLocalStorage } from './src/LocalStorage';
import { LocalStorageKeys } from './src/Enums/LocalStorageKeys';
import { PreferencesContext } from './src/PreferencesContext';

const { LightTheme, DarkTheme } = adaptNavigationTheme({
    reactNavigationLight: NavigationDefaultTheme,
    reactNavigationDark: NavigationDarkTheme
});

const CombinedDefaultTheme = {
    ...MD3LightTheme,
    ...LightTheme,
    colors: {
        ...MD3LightTheme.colors,
        ...LightTheme.colors,
    }
};

const CombinedDarkTheme = {
    ...MD3DarkTheme,
    ...DarkTheme,
    colors: {
        ...MD3DarkTheme.colors,
        ...DarkTheme.colors,
    }
};

function getComponentFunc() {
    const [isThemeDark, setIsThemeDark] = useState(getLocalStorageBoolean(LocalStorageKeys.isThemeDark));
    let theme = isThemeDark ? CombinedDarkTheme : CombinedDefaultTheme;
    const toggleTheme = useCallback(() => {
        let value = !isThemeDark;
        setLocalStorage(LocalStorageKeys.isThemeDark, value);
        return setIsThemeDark(value);
    }, [isThemeDark]);
    const preferences = useMemo(
        () => ({
            toggleTheme,
            isThemeDark,
        }),
        [toggleTheme, isThemeDark]
    );
    return (
        <Provider store={store}>
            <PreferencesContext.Provider value={preferences}>
                <PaperProvider theme={theme}>
                    <NavigationContainer theme={theme}>
                        <App />
                    </NavigationContainer>
                </PaperProvider>
            </PreferencesContext.Provider>
        </Provider>
    );
}

AppRegistry.registerComponent(name, () => getComponentFunc);
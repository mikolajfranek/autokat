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
import { RealmProvider } from '@realm/react';
import { CourseExchange } from './src/Database/Models/CourseExchange';
import { CourseMetal } from './src/Database/Models/CourseMetal';
import { Catalyst } from './src/Database/Models/Catalyst';
import { Filter } from './src/Database/Models/Filter';

const { LightTheme, DarkTheme } = adaptNavigationTheme({
    reactNavigationLight: NavigationDefaultTheme,
    reactNavigationDark: NavigationDarkTheme
});

const CombinedDefaultTheme = {
    ...MD3LightTheme,
    ...LightTheme,
    colors: {
        ...MD3LightTheme.colors,
        ...LightTheme.colors
    }
};

const CombinedDarkTheme = {
    ...MD3DarkTheme,
    ...DarkTheme,
    colors: {
        ...MD3DarkTheme.colors,
        ...DarkTheme.colors
    }
};

function getComponentFunc() {
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
            isThemeDark,
        }),
        [toggleTheme, isThemeDark]
    );
    return (
        <Provider store={store}>
            <PreferencesContext.Provider value={preferences}>
                <PaperProvider theme={currentTheme}>
                    <NavigationContainer theme={currentTheme}>
                        <RealmProvider schema={[CourseExchange, CourseMetal, Catalyst, Filter]}>
                            <App />
                        </RealmProvider>
                    </NavigationContainer>
                </PaperProvider>
            </PreferencesContext.Provider>
        </Provider>
    );
}

AppRegistry.registerComponent(name, () => getComponentFunc);
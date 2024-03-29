import 'react-native-gesture-handler'; //https://reactnavigation.org/docs/stack-navigator/
import { AppRegistry } from 'react-native';
import { name } from './app.json';
import { Provider } from 'react-redux';
import { store } from './src/store';
import App from './src';
import { MD3DarkTheme as DefaultTheme, PaperProvider } from 'react-native-paper';

const theme = {
    ...DefaultTheme,
    colors: {
        ...DefaultTheme.colors,
        primary: '#363636',
        secondary: '#1e3a8a'
    },
};

AppRegistry.registerComponent(name, () => () => (
    <Provider store={store}>
        <PaperProvider theme={theme}>
            <App />
        </PaperProvider>
    </Provider>
));
import 'react-native-gesture-handler'; //https://reactnavigation.org/docs/stack-navigator/
import { AppRegistry } from 'react-native';
import App from './Business';
import { name as appName } from './app.json';
import { store } from './Business/store';
import { Provider } from 'react-redux';

AppRegistry.registerComponent(appName, () => () => (
    <Provider store={store}>
        <App />
    </Provider>
));

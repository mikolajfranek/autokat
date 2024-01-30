/**
 * @format
 */

import { AppRegistry } from 'react-native';
import App from './Business';
import { name as appName } from './app.json';
import { store } from './app/store';
import { Provider } from 'react-redux';
import { GluestackUIProvider } from '@gluestack-ui/themed';
import { config } from '@gluestack-ui/config';

AppRegistry.registerComponent(appName, () => {
    <Provider store={store}>
        <GluestackUIProvider config={config}>
            <App />
        </GluestackUIProvider>
    </Provider>
});

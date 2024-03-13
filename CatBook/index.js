import { AppRegistry } from "react-native";
import { name } from "./app.json";
import { Provider } from "react-redux";
import { store } from "./src/store";
import App from "./src";

AppRegistry.registerComponent(name, () => () => (
    <Provider store={store}>
        <App />
    </Provider>
));
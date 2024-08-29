import React from 'react';
import { View } from 'react-native';
import { Text } from 'react-native-paper';

export default function App(): React.JSX.Element {
    return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
            <Text style={{ alignSelf: 'center' }} >
                Hello Katalizatory
            </Text>
        </View>
    );
}

/*


            <Divider style={{ width: '100%', marginBottom: 10, marginTop: 10, height: 1 }} />
            <Button
                icon='sync'
                onPress={async () => {
                    try {
                        //todo...
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (pełna)
            </Button>
            <Button
                icon='sync'
                onPress={async () => {
                    try {
                        //todo...
                    } catch (error) {
                        Alert.alert(
                            'Wystąpił błąd',
                            JSON.stringify(error));
                    }
                }}>
                Synchronizuj baze danych (przyrostowa)
            </Button>

*/
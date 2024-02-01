import { createContext, useContext, useState } from 'react';
import {
    Platform, TextInput,
} from 'react-native';
import * as React from 'react';
import { Button, View, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
const Stack = createNativeStackNavigator();

const message = Platform.select({
    ios: `Helloo world in IOS ${Platform.Version}`,
    android: `Helloo world in Android ${Platform.Version}`,
});




const ThemeContext = createContext("");

export default function App() {
    const [theme, setTheme] = useContext(ThemeContext);

    function HomeScreen({ navigation, route }) {

        React.useEffect(() => {
            if (route.params?.post) {
                console.log("Saving to storage. only if it changed....");
                // Post updated, do something with `route.params.post`
                // For example, send the post to the server
            }
        }, [route.params?.post]);

        return (
            <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
                <Text>Home Screen</Text>
                <Button
                    title="Go to Details"
                    onPress={() => {

                        navigation.navigate(
                            'Details',
                            {
                                itemId: 86,
                                otherParam: 'anything you want here',
                            })
                    }
                    }
                />

                <Button
                    title="Create post"
                    onPress={() => navigation.navigate('CreatePost')}
                />
                <Text style={{ margin: 10 }}>Post: {route.params?.post}</Text>
            </View>
        );
    }

    function DetailsScreen({ route, navigation }) {
        const { itemId, otherParam, query } = route.params;



        return (
            <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
                <Text>Details Screen</Text>
                <Text>itemId: {JSON.stringify(itemId)}</Text>
                <Text>otherParam: {JSON.stringify(otherParam)}</Text>
                <Text>query: {JSON.stringify(query)}</Text>
                <Button
                    title="Go to Details... again"
                    onPress={() => navigation.push('Details')}
                />
                <Button title="Go back" onPress={() => navigation.goBack()} />
                <Button
                    title="Go back to first screen in stack"
                    onPress={() => navigation.popToTop()}
                />
                <Button
                    title="set param in screen"
                    onPress={() => navigation.setParams({ query: "some" })}
                />
            </View>
        );
    }

    function CreatePostScreen({ navigation, route }) {
        const [postText, setPostText] = React.useState('');

        return (
            <>
                <TextInput
                    multiline
                    placeholder="What's on your mind?"
                    style={{ height: 200, padding: 10, backgroundColor: 'white' }}
                    value={postText}
                    onChangeText={setPostText}
                />
                <Button
                    title="Done"
                    onPress={() => {
                        // Pass and merge params back to home screen
                        navigation.navigate({
                            name: 'Home',
                            params: { post: postText },
                            merge: true,
                        });
                    }}
                />
            </>
        );
    }

    return (
        <ThemeContext.Provider value={theme}>
            <NavigationContainer>
                <Stack.Navigator
                    initialRouteName="Home"
                    screenOptions={{
                        headerStyle: { backgroundColor: 'lightblue' }
                    }}>

                    <Stack.Screen
                        name="Home"
                        component={HomeScreen} />

                    <Stack.Screen
                        name="Details"
                        initialParams={{ itemId: 42 }}
                        component={DetailsScreen} />

                    <Stack.Screen
                        name="CreatePost"
                        component={CreatePostScreen} />

                </Stack.Navigator>
            </NavigationContainer>
        </ThemeContext.Provider>
    );
}
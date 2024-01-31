import React from "react";
import { Button, View, Text, TextInput } from 'react-native';
import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
const Stack = createNativeStackNavigator();

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
                onPress={() => navigation.navigate('HomeCreatePost')}
            />
            <Text style={{ margin: 10 }}>Post: {route.params?.post}</Text>

            <Button
                onPress={() => navigation.navigate('MyModal')}
                title="Open Modal"
            />
        </View>
    );
}

function CreatePostScreen1({ navigation, route }) {
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

function CreatePostScreen2({ navigation, route }) {
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

function CreatePostScreen3({ navigation, route }) {

    useFocusEffect(
        React.useCallback(() => {
            // Do something when the screen is focused
            console.log("focus....");

            return () => {
                // Do something when the screen is unfocused
                console.log("clean....");
                // Useful for cleanup functions
            };
        }, []));


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
                    navigation.navigate('Settings', {
                        screen: 'SettingsHome',
                        params: { user: 'jane' },
                    })
                }}
            />
        </>
    );
}

function ModalScreen({ navigation }) {
    return (
        <View style={{ height: 100,  alignItems: 'center', justifyContent: 'center' }}>
            <Text style={{ fontSize: 30 }}>This is a modal!</Text>
            <Button onPress={() => navigation.goBack()} title="Dismiss" />
        </View>
        
    );
}

export default function Render({ navigation, route }) {
    return (
        <Stack.Navigator
            initialRouteName="HomeHome"
            screenOptions={{
                headerStyle: { backgroundColor: 'lightblue' },

            }}>

            <Stack.Group>
                <Stack.Screen
                    name="HomeHome"
                    component={HomeScreen} />
                <Stack.Screen
                    name="HomeDetails"
                    initialParams={{ itemId: 42 }}
                    component={CreatePostScreen2} />
                <Stack.Screen
                    name="HomeCreatePost"
                    component={CreatePostScreen3} />
            </Stack.Group>

            <Stack.Group screenOptions={{ presentation: 'modal', headerShown: false }}>
                <Stack.Screen name="MyModal" component={ModalScreen} />
            </Stack.Group>




        </Stack.Navigator>
    );
}
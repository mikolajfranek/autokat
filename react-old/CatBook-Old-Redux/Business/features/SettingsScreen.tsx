import React from "react";
import { Button, View, Text, TextInput } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
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
                title="Create post in another navigator"
                onPress={() => 
                    navigation.navigate('Home', {
                        screen: 'HomeCreatePost',
                        params: { user: 'jane' },
                      })
                    
                    }
            />
            <Text style={{ margin: 10 }}>Post: {route.params?.post}</Text>
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

export default function Render({ navigation, route }) {
    return (
            <Stack.Navigator
                initialRouteName="SettingsHome"
                screenOptions={{
                    headerStyle: { backgroundColor: 'lightblue' },
                    
                }}>

                <Stack.Screen
                    name="SettingsHome"
                    component={HomeScreen} />

                <Stack.Screen
                    name="SettingsDetails"
                    initialParams={{ itemId: 42 }}
                    component={CreatePostScreen2} />

                <Stack.Screen
                    name="SettingsCreatePost"
                    component={CreatePostScreen3} />

            </Stack.Navigator>
    );
}
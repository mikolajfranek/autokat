import React, { useEffect, useRef, useState } from 'react';
import { View } from 'react-native';
import styles from './style';

export default function render(): React.JSX.Element {
    const isMounted = useRef(false);
    //const [isRendering, setIsRendering] = useState(false);

    useEffect(() => {
        let connection = null;
        //TODO connection = await getConnection();

        //cleanup function
        return () => {
            //TODO connection dispose
        };
    }, []);

    useEffect(() => {
        if (isMounted.current == false) {
            //TODO first mount
            isMounted.current = true;
        }

        //cleanup function
        return () => {
            //TODO 
        };
    }, []);

    useEffect(() => {
        let ignore = false;
        async function startFetching() {
            let data = null;
            //TODO data = await getFetch();
            if (ignore == false) {

            }
        }
        startFetching();

        //cleanup function
        return () => {
            //TODO 
            ignore = true;
        };
    }, []);

    return (
        <View>

        </View>
    );
}

import React, { useEffect, useRef, useState } from 'react';
import { View } from 'react-native';
import styles from './style';


export default function render(): React.JSX.Element {
    const isMounted = useRef(false);
    //const [isRendering, setIsRendering] = useState(false);

    useEffect(() => {
        let connection = null;
        if (isMounted.current == false) {
            //TODO connection init
            isMounted.current = true;
        }
        //cleanup function
        return () => {
            //TODO connection dispose
        };
    }, []);

    return (
        <View>

        </View>
    );
}

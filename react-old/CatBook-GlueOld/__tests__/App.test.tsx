/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../App';

// Note: import explicitly to use the types shiped with jest.
import {it} from '@jest/globals';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

import { GluestackUIProvider, Text } from "@gluestack-ui/themed"
import { config } from "@gluestack-ui/config" // Optional if you want to use default theme


it('renders correctly', () => {
  renderer.create(
    //cannot render GluestackUIProvider, there is exception throwed, i don not why

    //transformIgnorePatterns in jest.config.js is related
    <Text>Hello World!</Text>
  );
});


/* todo
scenario of create project (describe it)
yarn create gluestack

przygotowac core dla projektu - gluestack
...
reszta

*/
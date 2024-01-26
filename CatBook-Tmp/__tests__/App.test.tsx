/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../Business/GUI/Start';

// Note: import explicitly to use the types shipped with jest.
import { it, expect } from '@jest/globals';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

it('renders correctly', () => {
  renderer.create(<App />);
});

it('jednostkowy test', () => {
  expect(true).toBe(true);
});

it('migawkowy test', () => {
  const tree = renderer.create(<App />).toJSON();
  expect(tree).toMatchSnapshot();
});
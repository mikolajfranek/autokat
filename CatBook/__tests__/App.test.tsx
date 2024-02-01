/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../Business';

// Note: import explicitly to use the types shipped with jest.
import { it, expect } from '@jest/globals';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

it('renders correctly', () => {
  renderer.create(<App />);
});

it('unit test', () => {
  expect(true).toBe(true);
});

it('snapshot test', () => {
  const tree = renderer.create(<App />).toJSON();
  expect(tree).toMatchSnapshot();
});
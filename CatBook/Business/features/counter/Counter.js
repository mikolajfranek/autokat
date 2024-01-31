import React from 'react'
import { useAppSelector, useAppDispatch } from '../..//hooks'
import { decrement, increment } from './counterSlice'
import { Button, Text, View } from 'react-native';

export function Counter() {
  const count = useAppSelector((state) => state.counter.value)
  const dispatch = useAppDispatch()

  return (
      <View>
        <Button
          onClick={() => dispatch(increment())}
        >
          Increment
        </Button>
        <span>{count}</span>

        <Button
          onClick={() => dispatch(decrement())}
        >
          Decrement
        </Button>
      </View>
  )
}
import React, { useState } from 'react'
import { useAppSelector, useAppDispatch } from '../../hooks'
import { decrement, increment, incrementByAmount, incrementAsync } from './counterSlice'
import { Button, Text, View, TextInput} from 'react-native';

export function Counter() {
  const count = useAppSelector((state) => state.counter.value)
  const [incrementAmount, setIncrementAmount] = useState('2');
  const dispatch = useAppDispatch()

  return (
    <View>
      <Button title='Incement' onPress={() => 
        {
          console.log('clik');
          dispatch(increment());
        }
        }>
        <Text>Increment</Text>
      </Button>
      <Text>{count}</Text>

      <Button title='Decrement' 
        onPress={() => dispatch(decrement())}>
        <Text>Decrement</Text>
      </Button>

      <View>
        <TextInput
          value={incrementAmount}
        />
        <Button  title=' Add Amount' 
          onPress={() =>
            dispatch(incrementByAmount(Number(incrementAmount) || 0))
          }
        >
         
        </Button>
        <Button  title='     Add Async' 
          onPress={() => dispatch(incrementAsync(Number(incrementAmount) || 0))}
        >
        </Button>
      </View>
    </View>
  )
}
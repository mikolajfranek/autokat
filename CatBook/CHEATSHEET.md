## variables outside function
- multiple instances share the same variables
 
## useState (state as a snapshot)
- react waits until all code in the event handlers has run before processing your state updates
 
## useState (setStateName(x), where value x is not a function) 
- it is taken only last occurrence of update the same state
  
## useState (setStateName(x), where value x is a function) 
- update the same state multiple times 

## useRef
- remember some information and do not trigger new renders


//--------
## effects
- effects run at the end of a commit after the screen updates

## useEffect
delays a piece of code from running until that render is reflected on the screen

React will call your cleanup function each time before the Effect runs again, and one final time when the component unmounts (gets removed)

React always cleans up the previous render’s Effect before the next render’s Effect.

Type something into the input and then immediately press “Unmount the component”. Notice how unmounting cleans up the last render’s Effect. Here, it clears the last timeout before it has a chance to fire.


only last 'render' is processing
without cleanup function fires all queue states

Each Effect “captures” the text value from its corresponding render.  It doesn’t matter that the text state changed: an Effect from the render with text = 'ab' will always see 'ab'. In other words, Effects from each render are isolated from each other.


Each render has its own Effects 
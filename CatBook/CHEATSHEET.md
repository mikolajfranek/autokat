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
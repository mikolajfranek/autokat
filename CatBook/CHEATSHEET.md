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
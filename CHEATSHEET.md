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

## navidation
# params
Params are like options for a screen. They should only contain information to configure what's displayed in the screen. Avoid passing the full data which will be displayed on the screen itself (e.g. pass a user id instead of user object). Also avoid passing data which is used by multiple screens, such data should be in a global store. 

You can also think of the route object like a URL. If your screen had a URL, what should be in the URL? Params shouldn't contain data that you think should not be in the URL

Some examples of what should be in params are:

    IDs like user id, item id etc., e.g. navigation.navigate('Profile', { userId: 'Jane' })
    Params for sorting, filtering data etc. when you have a list of items, e.g. navigation.navigate('Feeds', { sortBy: 'latest' })
    Timestamps, page numbers or cursors for pagination, e.g. navigation.navigate('Chat', { beforeTime: 1603897152675 })
    Data to fill inputs on a screen to compose something, e.g. navigation.navigate('ComposeTweet', { title: 'Hello world!' })


    Redux is a pattern and library for managing and updating application state, using events called "actions"

    The only way to update the state is to call store.dispatch() and pass in an action object.

    The store calls the root reducer once, and saves the return value as its initial state
    
    When the UI is first rendered, UI components access the current state of the Redux store, and use that data to decide what to render. They also subscribe to any future store updates so they can know if the state has changed.

    The store runs the reducer function again with the previous state and the current action, and saves the return value as the new state

    The store notifies all parts of the UI that are subscribed that the store has been updated

    A "slice" is a collection of Redux reducer logic and actions for a single feature in your app,

    You can only write "mutating" logic in Redux Toolkit's createSlice and createReducer because they use Immer inside! If you write mutating logic in reducers without Immer, it will mutate the state and cause bugs!

    NO. Global state that is needed across the app should go in the Redux store. State that's only needed in one place should be kept in component state.
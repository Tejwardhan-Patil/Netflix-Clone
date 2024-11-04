import { configureStore, combineReducers } from '@reduxjs/toolkit';
import thunk from 'redux-thunk';
import logger from 'redux-logger';

// Initial States
const authInitialState = {
  user: null,
  isAuthenticated: false,
  loading: false,
  error: null,
};

const videoInitialState = {
  videos: [],
  loading: false,
  error: null,
};

const recommendationInitialState = {
  recommendations: [],
  loading: false,
  error: null,
};

// Action Types
const LOGIN_REQUEST = 'LOGIN_REQUEST';
const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
const LOGIN_FAILURE = 'LOGIN_FAILURE';
const LOGOUT = 'LOGOUT';

const FETCH_VIDEOS_REQUEST = 'FETCH_VIDEOS_REQUEST';
const FETCH_VIDEOS_SUCCESS = 'FETCH_VIDEOS_SUCCESS';
const FETCH_VIDEOS_FAILURE = 'FETCH_VIDEOS_FAILURE';

const FETCH_RECOMMENDATIONS_REQUEST = 'FETCH_RECOMMENDATIONS_REQUEST';
const FETCH_RECOMMENDATIONS_SUCCESS = 'FETCH_RECOMMENDATIONS_SUCCESS';
const FETCH_RECOMMENDATIONS_FAILURE = 'FETCH_RECOMMENDATIONS_FAILURE';

// Auth Reducer
const authReducer = (state = authInitialState, action) => {
  switch (action.type) {
    case LOGIN_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case LOGIN_SUCCESS:
      return {
        ...state,
        loading: false,
        isAuthenticated: true,
        user: action.payload,
      };
    case LOGIN_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    case LOGOUT:
      return {
        ...state,
        user: null,
        isAuthenticated: false,
      };
    default:
      return state;
  }
};

// Video Reducer
const videoReducer = (state = videoInitialState, action) => {
  switch (action.type) {
    case FETCH_VIDEOS_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case FETCH_VIDEOS_SUCCESS:
      return {
        ...state,
        loading: false,
        videos: action.payload,
      };
    case FETCH_VIDEOS_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    default:
      return state;
  }
};

// Recommendation Reducer
const recommendationReducer = (state = recommendationInitialState, action) => {
  switch (action.type) {
    case FETCH_RECOMMENDATIONS_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case FETCH_RECOMMENDATIONS_SUCCESS:
      return {
        ...state,
        loading: false,
        recommendations: action.payload,
      };
    case FETCH_RECOMMENDATIONS_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    default:
      return state;
  }
};

// Combined Reducers
const rootReducer = combineReducers({
  auth: authReducer,
  videos: videoReducer,
  recommendations: recommendationReducer,
});

// Redux store configuration
const middleware = [thunk];
if (process.env.NODE_ENV === 'development') {
  middleware.push(logger);
}

const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }).concat(middleware),
  devTools: process.env.NODE_ENV !== 'production',
});

export default store;
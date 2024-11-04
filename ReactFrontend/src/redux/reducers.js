import { combineReducers } from 'redux';

// Action Types
const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
const LOGIN_FAILURE = 'LOGIN_FAILURE';
const LOGOUT = 'LOGOUT';
const FETCH_VIDEOS_SUCCESS = 'FETCH_VIDEOS_SUCCESS';
const FETCH_VIDEOS_FAILURE = 'FETCH_VIDEOS_FAILURE';
const FETCH_RECOMMENDATIONS_SUCCESS = 'FETCH_RECOMMENDATIONS_SUCCESS';
const FETCH_RECOMMENDATIONS_FAILURE = 'FETCH_RECOMMENDATIONS_FAILURE';
const PLAY_VIDEO = 'PLAY_VIDEO';
const PAUSE_VIDEO = 'PAUSE_VIDEO';
const STOP_VIDEO = 'STOP_VIDEO';

// Initial States
const initialUserState = {
  isAuthenticated: false,
  userDetails: null,
  error: null,
};

const initialVideoState = {
  videos: [],
  currentVideo: null,
  error: null,
};

const initialRecommendationState = {
  recommendations: [],
  error: null,
};

// User Reducer
const userReducer = (state = initialUserState, action) => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...state,
        isAuthenticated: true,
        userDetails: action.payload,
        error: null,
      };
    case LOGIN_FAILURE:
      return {
        ...state,
        isAuthenticated: false,
        userDetails: null,
        error: action.payload,
      };
    case LOGOUT:
      return {
        ...state,
        isAuthenticated: false,
        userDetails: null,
        error: null,
      };
    default:
      return state;
  }
};

// Video Reducer
const videoReducer = (state = initialVideoState, action) => {
  switch (action.type) {
    case FETCH_VIDEOS_SUCCESS:
      return {
        ...state,
        videos: action.payload,
        error: null,
      };
    case FETCH_VIDEOS_FAILURE:
      return {
        ...state,
        videos: [],
        error: action.payload,
      };
    case PLAY_VIDEO:
      return {
        ...state,
        currentVideo: action.payload,
      };
    case PAUSE_VIDEO:
      return {
        ...state,
        currentVideo: { ...state.currentVideo, isPlaying: false },
      };
    case STOP_VIDEO:
      return {
        ...state,
        currentVideo: null,
      };
    default:
      return state;
  }
};

// Recommendations Reducer
const recommendationReducer = (state = initialRecommendationState, action) => {
  switch (action.type) {
    case FETCH_RECOMMENDATIONS_SUCCESS:
      return {
        ...state,
        recommendations: action.payload,
        error: null,
      };
    case FETCH_RECOMMENDATIONS_FAILURE:
      return {
        ...state,
        recommendations: [],
        error: action.payload,
      };
    default:
      return state;
  }
};

// Combine Reducers
const rootReducer = combineReducers({
  user: userReducer,
  videos: videoReducer,
  recommendations: recommendationReducer,
});

export default rootReducer;
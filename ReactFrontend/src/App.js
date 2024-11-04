import React, { useEffect } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { createStore, applyMiddleware } from 'redux';
import { Provider, useDispatch, useSelector } from 'react-redux';
import thunk from 'redux-thunk';
import { composeWithDevTools } from 'redux-devtools-extension';
import axios from 'axios';
import './App.css';

// Redux Action Types
const FETCH_VIDEOS_SUCCESS = 'FETCH_VIDEOS_SUCCESS';
const FETCH_VIDEOS_FAILURE = 'FETCH_VIDEOS_FAILURE';
const FETCH_RECOMMENDATIONS_SUCCESS = 'FETCH_RECOMMENDATIONS_SUCCESS';
const FETCH_RECOMMENDATIONS_FAILURE = 'FETCH_RECOMMENDATIONS_FAILURE';

// Redux Actions
const fetchVideos = () => async (dispatch) => {
  try {
    const response = await axios.get('/api/videos');
    dispatch({ type: FETCH_VIDEOS_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({ type: FETCH_VIDEOS_FAILURE, payload: error.message });
  }
};

const fetchRecommendations = () => async (dispatch) => {
  try {
    const response = await axios.get('/api/recommendations');
    dispatch({ type: FETCH_RECOMMENDATIONS_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({ type: FETCH_RECOMMENDATIONS_FAILURE, payload: error.message });
  }
};

// Redux Reducers
const initialState = {
  videos: [],
  recommendations: [],
  error: null,
};

const rootReducer = (state = initialState, action) => {
  switch (action.type) {
    case FETCH_VIDEOS_SUCCESS:
      return { ...state, videos: action.payload };
    case FETCH_VIDEOS_FAILURE:
      return { ...state, error: action.payload };
    case FETCH_RECOMMENDATIONS_SUCCESS:
      return { ...state, recommendations: action.payload };
    case FETCH_RECOMMENDATIONS_FAILURE:
      return { ...state, error: action.payload };
    default:
      return state;
  }
};

// Redux Store
const store = createStore(
  rootReducer,
  composeWithDevTools(applyMiddleware(thunk))
);

// Components
const Header = () => {
  return (
    <header className="app-header">
      <nav>
        <ul>
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <a href="/profile">Profile</a>
          </li>
        </ul>
      </nav>
    </header>
  );
};

const VideoPlayer = ({ videos }) => {
  return (
    <div className="video-player">
      {videos.map((video) => (
        <div key={video.id} className="video-item">
          <video controls>
            <source src={video.url} type="video/mp4" />
            Your browser does not support the video tag.
          </video>
          <p>{video.title}</p>
        </div>
      ))}
    </div>
  );
};

const RecommendationList = ({ recommendations }) => {
  return (
    <div className="recommendation-list">
      <h3>Recommended for you</h3>
      <ul>
        {recommendations.map((rec) => (
          <li key={rec.id}>{rec.title}</li>
        ))}
      </ul>
    </div>
  );
};

const HomePage = () => {
  return (
    <div className="home-page">
      <h1>Welcome to the Video Streaming Platform</h1>
    </div>
  );
};

const VideoDetailsPage = ({ match }) => {
  const videoId = match.params.id;
  return (
    <div className="video-details-page">
      <h1>Video Details for ID: {videoId}</h1>
    </div>
  );
};

const ProfilePage = () => {
  return (
    <div className="profile-page">
      <h1>Your Profile</h1>
    </div>
  );
};

const App = () => {
  const dispatch = useDispatch();
  const videos = useSelector((state) => state.videos);
  const recommendations = useSelector((state) => state.recommendations);

  useEffect(() => {
    dispatch(fetchVideos());
    dispatch(fetchRecommendations());
  }, [dispatch]);

  return (
    <Router>
      <div className="app">
        <Header />
        <Switch>
          <Route path="/" exact component={HomePage} />
          <Route path="/video/:id" component={VideoDetailsPage} />
          <Route path="/profile" component={ProfilePage} />
        </Switch>
        <div className="main-content">
          <VideoPlayer videos={videos} />
          <RecommendationList recommendations={recommendations} />
        </div>
      </div>
    </Router>
  );
};

// Wrap App in Redux Provider
const AppWrapper = () => (
  <Provider store={store}>
    <App />
  </Provider>
);

export default AppWrapper;
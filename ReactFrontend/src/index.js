import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom';
import { Provider, useDispatch, useSelector } from 'react-redux';
import { BrowserRouter as Router, Route, Switch, Link, useParams } from 'react-router-dom';
import { createStore, applyMiddleware, combineReducers } from 'redux';
import thunk from 'redux-thunk';
import axios from 'axios';
import './index.css';

// Redux Store and Reducers
const initialVideoState = {
  videoList: [],
  currentVideo: null,
};

const initialUserState = {
  isAuthenticated: false,
  userDetails: {},
};

const videoReducer = (state = initialVideoState, action) => {
  switch (action.type) {
    case 'SET_VIDEO_LIST':
      return {
        ...state,
        videoList: action.payload,
      };
    case 'SET_CURRENT_VIDEO':
      return {
        ...state,
        currentVideo: action.payload,
      };
    default:
      return state;
  }
};

const userReducer = (state = initialUserState, action) => {
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        isAuthenticated: true,
        userDetails: action.payload,
      };
    case 'LOGOUT':
      return {
        ...state,
        isAuthenticated: false,
        userDetails: {},
      };
    default:
      return state;
  }
};

const rootReducer = combineReducers({
  videos: videoReducer,
  user: userReducer,
});

const store = createStore(rootReducer, applyMiddleware(thunk));

// Action creators for async API calls
const fetchVideoList = () => async (dispatch) => {
  const response = await axios.get('https://website.com/api/videos');
  dispatch({ type: 'SET_VIDEO_LIST', payload: response.data });
};

const fetchVideoDetails = (id) => async (dispatch) => {
  const response = await axios.get(`https://website.com/api/videos/${id}`);
  dispatch({ type: 'SET_CURRENT_VIDEO', payload: response.data });
};

// Components
const Header = () => (
  <header>
    <h2>Netflix Clone</h2>
    <nav>
      <ul>
        <li><Link to="/">Home</Link></li>
        <li><Link to="/login">Login</Link></li>
        <li><Link to="/register">Register</Link></li>
        <li><Link to="/recommendations">Recommendations</Link></li>
      </ul>
    </nav>
  </header>
);

const Footer = () => (
  <footer>
    <p>&copy; 2024 Netflix Clone. All rights reserved.</p>
  </footer>
);

const Home = () => {
  const dispatch = useDispatch();
  const videoList = useSelector((state) => state.videos.videoList);

  useEffect(() => {
    dispatch(fetchVideoList());
  }, [dispatch]);

  return (
    <div>
      <h1>Welcome to Netflix Clone</h1>
      <div className="video-list">
        {videoList.map((video) => (
          <div key={video.id}>
            <Link to={`/video/${video.id}`}>
              <h3>{video.title}</h3>
              <img src={video.thumbnailUrl} alt={video.title} />
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};

const VideoPlayer = ({ video }) => (
  <div>
    <h2>{video.title}</h2>
    <video width="600" controls>
      <source src={video.videoUrl} type="video/mp4" />
      Your browser does not support the video tag.
    </video>
  </div>
);

const RecommendationList = () => {
  const [recommendations, setRecommendations] = useState([]);

  useEffect(() => {
    const fetchRecommendations = async () => {
      const response = await axios.get('https://website.com/api/recommendations');
      setRecommendations(response.data);
    };
    fetchRecommendations();
  }, []);

  return (
    <div>
      <h2>Recommended for You</h2>
      <div className="recommendation-list">
        {recommendations.map((rec) => (
          <div key={rec.id}>
            <h3>{rec.title}</h3>
            <img src={rec.thumbnailUrl} alt={rec.title} />
          </div>
        ))}
      </div>
    </div>
  );
};

const VideoDetails = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const video = useSelector((state) => state.videos.currentVideo);

  useEffect(() => {
    dispatch(fetchVideoDetails(id));
  }, [dispatch, id]);

  if (!video) {
    return <div>Loading...</div>;
  }

  return <VideoPlayer video={video} />;
};

const Login = () => (
  <div>
    <h2>Login</h2>
    <form>
      <label>Email:</label>
      <input type="email" name="email" required />
      <label>Password:</label>
      <input type="password" name="password" required />
      <button type="submit">Login</button>
    </form>
  </div>
);

const Register = () => (
  <div>
    <h2>Register</h2>
    <form>
      <label>Username:</label>
      <input type="text" name="username" required />
      <label>Email:</label>
      <input type="email" name="email" required />
      <label>Password:</label>
      <input type="password" name="password" required />
      <button type="submit">Register</button>
    </form>
  </div>
);

const NotFound = () => (
  <div>
    <h2>404 - Not Found</h2>
    <p>The page you're looking for does not exist.</p>
  </div>
);

// Main render method
ReactDOM.render(
  <Provider store={store}>
    <Router>
      <Header />
      <Switch>
        <Route exact path="/" component={Home} />
        <Route path="/video/:id" component={VideoDetails} />
        <Route path="/login" component={Login} />
        <Route path="/register" component={Register} />
        <Route path="/recommendations" component={RecommendationList} />
        <Route path="*" component={NotFound} />
      </Switch>
      <Footer />
    </Router>
  </Provider>,
  document.getElementById('root')
);
import axios from 'axios';

const API_URL = 'https://api.website.com'; 

// Create an instance of Axios with default settings
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // Timeout after 10 seconds
});

// Add a request interceptor for adding Authorization header
api.interceptors.request.use(
  async (config) => {
    const token = localStorage.getItem('token'); 
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor for handling errors globally
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    // Log error or display error notification
    if (error.response && error.response.status === 401) {
      // Handle unauthorized access
      window.location.href = '/login'; // Redirect to login page
    }
    return Promise.reject(error);
  }
);

// API Service functions

/**
 * Registers a new user.
 * @param {Object} userData - User registration details.
 * @returns {Promise} Axios promise.
 */
export const registerUser = async (userData) => {
  try {
    const response = await api.post('/auth/register', userData);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Logs in a user.
 * @param {Object} credentials - User login details.
 * @returns {Promise} Axios promise.
 */
export const loginUser = async (credentials) => {
  try {
    const response = await api.post('/auth/login', credentials);
    localStorage.setItem('token', response.token); // Save token to local storage
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Logs out a user.
 */
export const logoutUser = () => {
  localStorage.removeItem('token'); // Remove token from local storage
  window.location.href = '/login'; // Redirect to login page
};

/**
 * Fetches a list of videos.
 * @returns {Promise} Axios promise.
 */
export const getVideos = async () => {
  try {
    const response = await api.get('/videos');
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Fetches video details by video ID.
 * @param {string} videoId - The ID of the video.
 * @returns {Promise} Axios promise.
 */
export const getVideoDetails = async (videoId) => {
  try {
    const response = await api.get(`/videos/${videoId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Fetches a list of recommended videos based on user preferences.
 * @returns {Promise} Axios promise.
 */
export const getRecommendations = async () => {
  try {
    const response = await api.get('/recommendations');
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Updates user profile information.
 * @param {Object} profileData - New user profile data.
 * @returns {Promise} Axios promise.
 */
export const updateProfile = async (profileData) => {
  try {
    const response = await api.put('/user/profile', profileData);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Uploads a new video to the platform.
 * @param {FormData} videoData - Video file and metadata.
 * @returns {Promise} Axios promise.
 */
export const uploadVideo = async (videoData) => {
  try {
    const response = await api.post('/videos/upload', videoData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Deletes a video by its ID.
 * @param {string} videoId - The ID of the video to delete.
 * @returns {Promise} Axios promise.
 */
export const deleteVideo = async (videoId) => {
  try {
    const response = await api.delete(`/videos/${videoId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Fetches user analytics data.
 * @returns {Promise} Axios promise.
 */
export const getUserAnalytics = async () => {
  try {
    const response = await api.get('/analytics/user');
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Fetches the analytics of a specific video.
 * @param {string} videoId - The ID of the video.
 * @returns {Promise} Axios promise.
 */
export const getVideoAnalytics = async (videoId) => {
  try {
    const response = await api.get(`/analytics/video/${videoId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Handles video transcoding request.
 * @param {string} videoId - The ID of the video.
 * @returns {Promise} Axios promise.
 */
export const transcodeVideo = async (videoId) => {
  try {
    const response = await api.post(`/transcoding/${videoId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Fetches the status of a transcoding job.
 * @param {string} jobId - The ID of the transcoding job.
 * @returns {Promise} Axios promise.
 */
export const getTranscodingStatus = async (jobId) => {
  try {
    const response = await api.get(`/transcoding/status/${jobId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Subscribes a user to a notification service.
 * @param {Object} subscriptionData - Notification subscription data.
 * @returns {Promise} Axios promise.
 */
export const subscribeToNotifications = async (subscriptionData) => {
  try {
    const response = await api.post('/notifications/subscribe', subscriptionData);
    return response;
  } catch (error) {
    throw error;
  }
};

/**
 * Unsubscribes a user from the notification service.
 * @param {string} subscriptionId - The ID of the subscription to cancel.
 * @returns {Promise} Axios promise.
 */
export const unsubscribeFromNotifications = async (subscriptionId) => {
  try {
    const response = await api.delete(`/notifications/unsubscribe/${subscriptionId}`);
    return response;
  } catch (error) {
    throw error;
  }
};

export default api;
import React, { useState, useRef, useEffect } from 'react';
import { fetchVideoMetadata, logVideoProgress } from '../services/api'; 

const VideoPlayer = ({ videoId }) => {
  const [videoData, setVideoData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [playing, setPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  const [volume, setVolume] = useState(1);
  const [muted, setMuted] = useState(false);
  const [fullScreen, setFullScreen] = useState(false);
  
  const videoRef = useRef(null);
  const progressRef = useRef(null);
  const containerRef = useRef(null);

  useEffect(() => {
    // Fetch video metadata
    fetchVideoMetadata(videoId).then(data => {
      setVideoData(data);
      setLoading(false);
    }).catch(error => {
      console.error('Error fetching video data:', error);
      setLoading(false);
    });
  }, [videoId]);

  const handlePlayPause = () => {
    if (playing) {
      videoRef.current.pause();
    } else {
      videoRef.current.play();
    }
    setPlaying(!playing);
  };

  const handleProgress = () => {
    const current = videoRef.current.currentTime;
    const total = videoRef.current.duration;
    setProgress((current / total) * 100);

    // Log video progress for analytics
    logVideoProgress(videoId, current);
  };

  const handleVolumeChange = (event) => {
    const volumeLevel = event.target.value;
    videoRef.current.volume = volumeLevel;
    setVolume(volumeLevel);
  };

  const toggleMute = () => {
    setMuted(!muted);
    videoRef.current.muted = !muted;
  };

  const handleFullscreen = () => {
    if (!fullScreen) {
      containerRef.current.requestFullscreen();
    } else {
      document.exitFullscreen();
    }
    setFullScreen(!fullScreen);
  };

  const handleProgressBarClick = (event) => {
    const { left, width } = progressRef.current.getBoundingClientRect();
    const clickPosition = (event.clientX - left) / width;
    const newTime = clickPosition * videoRef.current.duration;
    videoRef.current.currentTime = newTime;
    setProgress(clickPosition * 100);
  };

  return (
    <div ref={containerRef} style={styles.container}>
      {loading ? (
        <div style={styles.loader}>Loading video...</div>
      ) : (
        <>
          <video
            ref={videoRef}
            src={videoData?.url}
            poster={videoData?.poster}
            onTimeUpdate={handleProgress}
            onClick={handlePlayPause}
            style={styles.video}
          />
          <div style={styles.controls}>
            <button onClick={handlePlayPause} style={styles.button}>
              {playing ? 'Pause' : 'Play'}
            </button>
            <div ref={progressRef} style={styles.progressBar} onClick={handleProgressBarClick}>
              <div style={{ ...styles.progress, width: `${progress}%` }}></div>
            </div>
            <div style={styles.volumeControls}>
              <input
                type="range"
                min="0"
                max="1"
                step="0.01"
                value={volume}
                onChange={handleVolumeChange}
                style={styles.volumeSlider}
              />
              <button onClick={toggleMute} style={styles.button}>
                {muted ? 'Unmute' : 'Mute'}
              </button>
            </div>
            <button onClick={handleFullscreen} style={styles.button}>
              {fullScreen ? 'Exit Fullscreen' : 'Fullscreen'}
            </button>
          </div>
        </>
      )}
    </div>
  );
};

const styles = {
  container: {
    position: 'relative',
    width: '100%',
    maxWidth: '800px',
    margin: '0 auto',
    backgroundColor: 'black',
  },
  video: {
    width: '100%',
    height: 'auto',
  },
  controls: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '10px',
    backgroundColor: 'rgba(0, 0, 0, 0.6)',
    position: 'absolute',
    bottom: 0,
    width: '100%',
    boxSizing: 'border-box',
  },
  button: {
    background: 'none',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    fontSize: '16px',
    marginRight: '10px',
  },
  buttonHover: {
    color: 'lightgray',
  },
  progressBar: {
    width: '60%',
    height: '5px',
    backgroundColor: 'gray',
    cursor: 'pointer',
    position: 'relative',
    margin: '0 10px',
  },
  progress: {
    backgroundColor: 'red',
    height: '100%',
    width: '0%',
  },
  volumeControls: {
    display: 'flex',
    alignItems: 'center',
  },
  volumeSlider: {
    marginRight: '10px',
    cursor: 'pointer',
  },
  loader: {
    color: 'white',
    fontSize: '18px',
    textAlign: 'center',
    padding: '20px',
  },
};

export default VideoPlayer;
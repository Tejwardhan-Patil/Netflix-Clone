package controllers;

import domain.Video;
import services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    // Get all videos
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // Get a video by ID
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        Optional<Video> video = videoService.getVideoById(id);
        return video.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Create a new video
    @PostMapping
    public ResponseEntity<Video> addVideo(@RequestBody Video video) {
        Video createdVideo = videoService.addVideo(video);
        return new ResponseEntity<>(createdVideo, HttpStatus.CREATED);
    }

    // Update a video by ID
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video updatedVideo) {
        Optional<Video> existingVideo = videoService.getVideoById(id);
        if (existingVideo.isPresent()) {
            Video video = existingVideo.get();
            video.setTitle(updatedVideo.getTitle());
            video.setDescription(updatedVideo.getDescription());
            video.setUrl(updatedVideo.getUrl());
            video.setDuration(updatedVideo.getDuration());
            video.setReleaseDate(updatedVideo.getReleaseDate());

            Video savedVideo = videoService.updateVideo(video);
            return new ResponseEntity<>(savedVideo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a video by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        Optional<Video> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            videoService.deleteVideo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Search videos by title
    @GetMapping("/search")
    public ResponseEntity<List<Video>> searchVideosByTitle(@RequestParam String title) {
        List<Video> videos = videoService.searchVideosByTitle(title);
        if (!videos.isEmpty()) {
            return new ResponseEntity<>(videos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Retrieve videos by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Video>> getVideosByGenre(@PathVariable String genre) {
        List<Video> videos = videoService.getVideosByGenre(genre);
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // Retrieve videos released in a specific year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Video>> getVideosByReleaseYear(@PathVariable int year) {
        List<Video> videos = videoService.getVideosByReleaseYear(year);
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // Retrieve most popular videos
    @GetMapping("/popular")
    public ResponseEntity<List<Video>> getPopularVideos() {
        List<Video> popularVideos = videoService.getPopularVideos();
        return new ResponseEntity<>(popularVideos, HttpStatus.OK);
    }

    // Retrieve recently added videos
    @GetMapping("/recent")
    public ResponseEntity<List<Video>> getRecentlyAddedVideos() {
        List<Video> recentVideos = videoService.getRecentlyAddedVideos();
        return new ResponseEntity<>(recentVideos, HttpStatus.OK);
    }

    // Add a like to a video
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeVideo(@PathVariable Long id) {
        Optional<Video> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            videoService.likeVideo(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Add a dislike to a video
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Void> dislikeVideo(@PathVariable Long id) {
        Optional<Video> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            videoService.dislikeVideo(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Mark a video as watched
    @PostMapping("/{id}/watched")
    public ResponseEntity<Void> markAsWatched(@PathVariable Long id) {
        Optional<Video> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            videoService.markAsWatched(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Retrieve watched videos by a user
    @GetMapping("/watched/{userId}")
    public ResponseEntity<List<Video>> getWatchedVideos(@PathVariable Long userId) {
        List<Video> watchedVideos = videoService.getWatchedVideos(userId);
        return new ResponseEntity<>(watchedVideos, HttpStatus.OK);
    }

    // Get recommended videos for a user
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<List<Video>> getRecommendedVideos(@PathVariable Long userId) {
        List<Video> recommendedVideos = videoService.getRecommendedVideos(userId);
        return new ResponseEntity<>(recommendedVideos, HttpStatus.OK);
    }
}
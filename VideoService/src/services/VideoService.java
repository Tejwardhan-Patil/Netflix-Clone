package services;

import videoservice.domain.Video;
import videoservice.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Path videoStorageLocation;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        this.videoStorageLocation = Paths.get("videos").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.videoStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory for storing uploaded videos.", e);
        }
    }

    public Video uploadVideo(MultipartFile file, String title, String description) {
        String fileName = storeFile(file);
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFileName(fileName);
        return videoRepository.save(video);
    }

    private String storeFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            if (fileName != null) {
                Path targetLocation = this.videoStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again.", ex);
        }
        return fileName;
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }

    @Transactional
    public Video updateVideo(Long id, String title, String description) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setTitle(title);
            video.setDescription(description);
            return videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + id + " not found.");
        }
    }

    public void deleteVideo(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            deleteFile(video.getFileName());
            videoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Video with id " + id + " not found.");
        }
    }

    private void deleteFile(String fileName) {
        try {
            Path filePath = this.videoStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file " + fileName, e);
        }
    }

    public Video likeVideo(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setLikes(video.getLikes() + 1);
            return videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + id + " not found.");
        }
    }

    public Video dislikeVideo(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setDislikes(video.getDislikes() + 1);
            return videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + id + " not found.");
        }
    }

    public List<Video> searchVideos(String keyword) {
        return videoRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Video> getMostLikedVideos() {
        return videoRepository.findTop10ByOrderByLikesDesc();
    }

    public List<Video> getMostRecentVideos() {
        return videoRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public long countAllVideos() {
        return videoRepository.count();
    }

    public boolean videoExists(Long id) {
        return videoRepository.existsById(id);
    }

    public List<Video> getVideosByUser(Long userId) {
        return videoRepository.findByUserId(userId);
    }

    public List<Video> getVideosByCategory(String category) {
        return videoRepository.findByCategory(category);
    }

    public void assignCategoryToVideo(Long videoId, String category) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setCategory(category);
            videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + videoId + " not found.");
        }
    }
    
    public void removeCategoryFromVideo(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setCategory(null);
            videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + videoId + " not found.");
        }
    }
    
    public Video setVideoAsFeatured(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setFeatured(true);
            return videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + videoId + " not found.");
        }
    }

    public Video unsetVideoAsFeatured(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setFeatured(false);
            return videoRepository.save(video);
        } else {
            throw new RuntimeException("Video with id " + videoId + " not found.");
        }
    }
}
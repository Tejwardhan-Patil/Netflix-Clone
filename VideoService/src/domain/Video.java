package domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String uploadedBy;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private int views;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_likes", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "user_id")
    private List<Long> likes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_dislikes", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "user_id")
    private List<Long> dislikes;

    @Column(nullable = false)
    private boolean isPublic;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "video")
    private List<Comment> comments;

    @Column(nullable = false)
    private double rating;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_ratings", joinColumns = @JoinColumn(name = "video_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "rating")
    private Map<Long, Double> userRatings;

    @Column(nullable = false)
    private int durationInSeconds;

    public Video() {
        // Default constructor for JPA
    }

    public Video(String title, String description, String videoUrl, String thumbnailUrl, LocalDateTime uploadedAt,
                 String uploadedBy, List<String> tags, int views, boolean isPublic, int durationInSeconds) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
        this.tags = tags;
        this.views = views;
        this.isPublic = isPublic;
        this.durationInSeconds = durationInSeconds;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<Long> getLikes() {
        return likes;
    }

    public void setLikes(List<Long> likes) {
        this.likes = likes;
    }

    public List<Long> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<Long> dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Map<Long, Double> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Map<Long, Double> userRatings) {
        this.userRatings = userRatings;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    // Methods for business logic

    public void addView() {
        this.views++;
    }

    public void addLike(Long userId) {
        if (!this.likes.contains(userId)) {
            this.likes.add(userId);
            this.dislikes.remove(userId); // Remove from dislikes if present
        }
    }

    public void addDislike(Long userId) {
        if (!this.dislikes.contains(userId)) {
            this.dislikes.add(userId);
            this.likes.remove(userId); // Remove from likes if present
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addRating(Long userId, double rating) {
        this.userRatings.put(userId, rating);
        recalculateRating();
    }

    private void recalculateRating() {
        double totalRating = this.userRatings.values().stream().mapToDouble(Double::doubleValue).sum();
        this.rating = totalRating / this.userRatings.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", uploadedAt=" + uploadedAt +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", views=" + views +
                ", isPublic=" + isPublic +
                ", rating=" + rating +
                ", durationInSeconds=" + durationInSeconds +
                '}';
    }
}
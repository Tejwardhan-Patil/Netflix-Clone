package repositories;

import videoservice.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Find all videos by category
    List<Video> findByCategory(String category);

    // Find all videos by language
    List<Video> findByLanguage(String language);

    // Find all videos by release year
    List<Video> findByReleaseYear(int year);

    // Find all videos by director
    List<Video> findByDirector(String director);

    // Custom query to search videos by title containing specific text
    @Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Video> searchByTitle(@Param("title") String title);

    // Custom query to find videos by rating above a certain threshold
    @Query("SELECT v FROM Video v WHERE v.rating >= :rating")
    List<Video> findByRatingGreaterThanEqual(@Param("rating") double rating);

    // Custom query to get top N most-watched videos
    @Query("SELECT v FROM Video v ORDER BY v.viewCount DESC")
    List<Video> findTopWatchedVideos(Pageable pageable);

    // Custom query to find videos by actor
    @Query("SELECT v FROM Video v JOIN v.actors a WHERE a.name = :actorName")
    List<Video> findByActor(@Param("actorName") String actorName);

    // Transactional method to increment view count of a video
    @Transactional
    @Modifying
    @Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.id = :videoId")
    void incrementViewCount(@Param("videoId") Long videoId);

    // Method to find videos by a list of tags
    @Query("SELECT v FROM Video v JOIN v.tags t WHERE t.name IN :tags")
    List<Video> findByTags(@Param("tags") List<String> tags);

    // Method to get a random video
    @Query(value = "SELECT * FROM Video ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Video> getRandomVideo();

    // Find all videos by availability status (available, unavailable)
    List<Video> findByAvailabilityStatus(String status);

    // Get the count of all videos available in a specific category
    @Query("SELECT COUNT(v) FROM Video v WHERE v.category = :category")
    long countByCategory(@Param("category") String category);

    // Custom method to find the most recent video uploads
    @Query("SELECT v FROM Video v ORDER BY v.uploadedAt DESC")
    List<Video> findMostRecentUploads(Pageable pageable);

    // Custom method to find videos by duration greater than a given length
    @Query("SELECT v FROM Video v WHERE v.duration > :duration")
    List<Video> findByDurationGreaterThan(@Param("duration") int duration);

    // Method to find videos with multiple filter criteria
    @Query("SELECT v FROM Video v WHERE v.category = :category AND v.language = :language AND v.rating >= :rating")
    List<Video> findByCategoryLanguageAndRating(@Param("category") String category, @Param("language") String language, @Param("rating") double rating);

    // Method to delete all videos by category
    @Transactional
    void deleteByCategory(String category);

    // Bulk update the status of videos by category
    @Transactional
    @Modifying
    @Query("UPDATE Video v SET v.availabilityStatus = :status WHERE v.category = :category")
    void updateStatusByCategory(@Param("status") String status, @Param("category") String category);

    // Find videos by multiple categories
    @Query("SELECT v FROM Video v WHERE v.category IN :categories")
    List<Video> findByMultipleCategories(@Param("categories") List<String> categories);

    // Custom query to find videos by a range of release years
    @Query("SELECT v FROM Video v WHERE v.releaseYear BETWEEN :startYear AND :endYear")
    List<Video> findByReleaseYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);

    // Find videos with subtitles in a specific language
    @Query("SELECT v FROM Video v JOIN v.subtitles s WHERE s.language = :subtitleLanguage")
    List<Video> findBySubtitleLanguage(@Param("subtitleLanguage") String subtitleLanguage);

    // Get a list of all available genres
    @Query("SELECT DISTINCT v.genre FROM Video v")
    List<String> findAllGenres();

    // Custom query to find videos by genre
    @Query("SELECT v FROM Video v WHERE v.genre = :genre")
    List<Video> findByGenre(@Param("genre") String genre);

    // Find videos by user rating
    @Query("SELECT v FROM Video v JOIN v.userRatings ur WHERE ur.rating >= :rating")
    List<Video> findByUserRating(@Param("rating") double rating);

    // Find videos by a range of view counts
    @Query("SELECT v FROM Video v WHERE v.viewCount BETWEEN :minViewCount AND :maxViewCount")
    List<Video> findByViewCountRange(@Param("minViewCount") long minViewCount, @Param("maxViewCount") long maxViewCount);

    // Custom query to find the longest video by category
    @Query("SELECT v FROM Video v WHERE v.category = :category ORDER BY v.duration DESC")
    List<Video> findLongestVideoByCategory(@Param("category") String category, Pageable pageable);

    // Transactional method to reset view count for all videos
    @Transactional
    @Modifying
    @Query("UPDATE Video v SET v.viewCount = 0")
    void resetViewCount();

    // Find videos uploaded by a specific user
    @Query("SELECT v FROM Video v WHERE v.uploaderId = :uploaderId")
    List<Video> findByUploader(@Param("uploaderId") Long uploaderId);

    // Method to fetch all videos with reviews greater than a certain threshold
    @Query("SELECT v FROM Video v JOIN v.reviews r WHERE r.rating >= :rating")
    List<Video> findByReviewRatingGreaterThanEqual(@Param("rating") double rating);

    // Method to fetch videos by a combination of actor and director
    @Query("SELECT v FROM Video v JOIN v.actors a WHERE a.name = :actorName AND v.director = :director")
    List<Video> findByActorAndDirector(@Param("actorName") String actorName, @Param("director") String director);
}
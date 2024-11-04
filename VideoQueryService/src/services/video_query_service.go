package services

import (
	"context"
	"database/sql"
	"errors"
	"log"
	"strings"
	"time"

	_ "github.com/lib/pq"
)

// Video represents the structure of video data
type Video struct {
	ID          string    `json:"id"`
	Title       string    `json:"title"`
	Description string    `json:"description"`
	Duration    int       `json:"duration"`
	CreatedAt   time.Time `json:"created_at"`
}

// VideoQueryService defines the interface for querying video data
type VideoQueryService interface {
	GetVideoByID(ctx context.Context, videoID string) (*Video, error)
	ListAllVideos(ctx context.Context) ([]*Video, error)
	SearchVideos(ctx context.Context, query string) ([]*Video, error)
}

// videoQueryServiceImpl implements the VideoQueryService interface
type videoQueryServiceImpl struct {
	repo VideoQueryRepository
}

// NewVideoQueryService creates a new instance of VideoQueryService
func NewVideoQueryService(repo VideoQueryRepository) VideoQueryService {
	return &videoQueryServiceImpl{repo: repo}
}

// GetVideoByID fetches a video by its ID from the repository
func (s *videoQueryServiceImpl) GetVideoByID(ctx context.Context, videoID string) (*Video, error) {
	if videoID == "" {
		return nil, errors.New("video ID cannot be empty")
	}

	video, err := s.repo.FetchVideoByID(ctx, videoID)
	if err != nil {
		log.Printf("error fetching video by ID: %v", err)
		return nil, err
	}

	if video == nil {
		return nil, errors.New("video not found")
	}

	return video, nil
}

// ListAllVideos fetches all videos from the repository
func (s *videoQueryServiceImpl) ListAllVideos(ctx context.Context) ([]*Video, error) {
	videos, err := s.repo.FetchAllVideos(ctx)
	if err != nil {
		log.Printf("error fetching all videos: %v", err)
		return nil, err
	}

	return videos, nil
}

// SearchVideos searches videos based on the given query
func (s *videoQueryServiceImpl) SearchVideos(ctx context.Context, query string) ([]*Video, error) {
	if query == "" {
		return nil, errors.New("query cannot be empty")
	}

	videos, err := s.repo.SearchVideos(ctx, query)
	if err != nil {
		log.Printf("error searching videos: %v", err)
		return nil, err
	}

	return videos, nil
}

// VideoQueryRepository defines the interface for querying video data
type VideoQueryRepository interface {
	FetchVideoByID(ctx context.Context, videoID string) (*Video, error)
	FetchAllVideos(ctx context.Context) ([]*Video, error)
	SearchVideos(ctx context.Context, query string) ([]*Video, error)
}

// videoQueryRepositoryImpl implements the VideoQueryRepository interface
type videoQueryRepositoryImpl struct {
	db *sql.DB
}

// NewVideoQueryRepository creates a new instance of VideoQueryRepository
func NewVideoQueryRepository(db *sql.DB) VideoQueryRepository {
	return &videoQueryRepositoryImpl{db: db}
}

// FetchVideoByID fetches a video by its ID from the database
func (r *videoQueryRepositoryImpl) FetchVideoByID(ctx context.Context, videoID string) (*Video, error) {
	query := "SELECT id, title, description, duration, created_at FROM videos WHERE id = ?"
	row := r.db.QueryRowContext(ctx, query, videoID)

	video := &Video{}
	err := row.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.CreatedAt)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, nil
		}
		return nil, err
	}

	return video, nil
}

// FetchAllVideos fetches all videos from the database
func (r *videoQueryRepositoryImpl) FetchAllVideos(ctx context.Context) ([]*Video, error) {
	query := "SELECT id, title, description, duration, created_at FROM videos"
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []*Video
	for rows.Next() {
		video := &Video{}
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.CreatedAt)
		if err != nil {
			return nil, err
		}
		videos = append(videos, video)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return videos, nil
}

// SearchVideos searches videos based on a query string from the database
func (r *videoQueryRepositoryImpl) SearchVideos(ctx context.Context, queryStr string) ([]*Video, error) {
	query := "SELECT id, title, description, duration, created_at FROM videos WHERE title LIKE ? OR description LIKE ?"
	rows, err := r.db.QueryContext(ctx, query, "%"+strings.ToLower(queryStr)+"%", "%"+strings.ToLower(queryStr)+"%")
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []*Video
	for rows.Next() {
		video := &Video{}
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.CreatedAt)
		if err != nil {
			return nil, err
		}
		videos = append(videos, video)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return videos, nil
}

func main() {
	// Database connection setup
	db, err := sql.Open("pq", "user:password@tcp(localhost:3306)/videodb")
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}
	defer db.Close()

	// Test the VideoQueryService
	repo := NewVideoQueryRepository(db)
	service := NewVideoQueryService(repo)

	ctx := context.Background()

	// Fetch video by ID
	video, err := service.GetVideoByID(ctx, "123")
	if err != nil {
		log.Fatal(err)
	}
	log.Printf("Video: %+v\n", video)

	// List all videos
	videos, err := service.ListAllVideos(ctx)
	if err != nil {
		log.Fatal(err)
	}
	for _, v := range videos {
		log.Printf("Video: %+v\n", v)
	}

	// Search videos by query
	searchResults, err := service.SearchVideos(ctx, "sample")
	if err != nil {
		log.Fatal(err)
	}
	for _, v := range searchResults {
		log.Printf("Search Result: %+v\n", v)
	}
}

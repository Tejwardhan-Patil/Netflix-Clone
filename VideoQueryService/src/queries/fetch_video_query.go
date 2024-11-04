package queries

import (
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"time"
)

// Video represents the structure of a video record
type Video struct {
	ID          string    `json:"id"`
	Title       string    `json:"title"`
	Description string    `json:"description"`
	Duration    int       `json:"duration"`
	UploadedAt  time.Time `json:"uploaded_at"`
	URL         string    `json:"url"`
}

// VideoQueryService defines the methods required to fetch video data
type VideoQueryService struct {
	db *sql.DB
}

// NewVideoQueryService creates a new instance of VideoQueryService
func NewVideoQueryService(db *sql.DB) *VideoQueryService {
	return &VideoQueryService{db: db}
}

// FetchVideoByID retrieves video details by its ID
func (s *VideoQueryService) FetchVideoByID(ctx context.Context, videoID string) (*Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos WHERE id = ?`

	var video Video
	row := s.db.QueryRowContext(ctx, query, videoID)
	err := row.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, errors.New("video not found")
		}
		return nil, err
	}
	return &video, nil
}

// FetchVideosByTitle searches for videos by title
func (s *VideoQueryService) FetchVideosByTitle(ctx context.Context, title string) ([]Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos WHERE title LIKE ?`
	rows, err := s.db.QueryContext(ctx, query, "%"+title+"%")
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
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

// FetchAllVideos retrieves all available videos from the database
func (s *VideoQueryService) FetchAllVideos(ctx context.Context) ([]Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos`
	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
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

// FetchVideosByDateRange retrieves videos uploaded within a specific date range
func (s *VideoQueryService) FetchVideosByDateRange(ctx context.Context, startDate, endDate time.Time) ([]Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos WHERE uploaded_at BETWEEN ? AND ?`
	rows, err := s.db.QueryContext(ctx, query, startDate, endDate)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
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

// FetchVideoByURL retrieves video details by its URL
func (s *VideoQueryService) FetchVideoByURL(ctx context.Context, videoURL string) (*Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos WHERE url = ?`
	var video Video
	row := s.db.QueryRowContext(ctx, query, videoURL)
	err := row.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
	if err != nil {
		if err == sql.ErrNoRows {
			return nil, errors.New("video not found")
		}
		return nil, err
	}
	return &video, nil
}

// FetchMostRecentVideos retrieves the most recently uploaded videos, limited by a number
func (s *VideoQueryService) FetchMostRecentVideos(ctx context.Context, limit int) ([]Video, error) {
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos ORDER BY uploaded_at DESC LIMIT ?`
	rows, err := s.db.QueryContext(ctx, query, limit)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
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

// FetchVideosWithPagination retrieves videos with pagination
func (s *VideoQueryService) FetchVideosWithPagination(ctx context.Context, page, pageSize int) ([]Video, error) {
	offset := (page - 1) * pageSize
	query := `SELECT id, title, description, duration, uploaded_at, url FROM videos LIMIT ? OFFSET ?`
	rows, err := s.db.QueryContext(ctx, query, pageSize, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.Duration, &video.UploadedAt, &video.URL)
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

// ServeHTTP handles HTTP requests for video queries
func (s *VideoQueryService) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	videoID := r.URL.Query().Get("id")

	if videoID == "" {
		http.Error(w, "missing video ID", http.StatusBadRequest)
		return
	}

	video, err := s.FetchVideoByID(ctx, videoID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(video)
}

// LogError is a helper function for error logging
func LogError(err error) {
	if err != nil {
		log.Println("Error:", err)
	}
}

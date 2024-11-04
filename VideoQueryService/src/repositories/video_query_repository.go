package repositories

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"time"

	"github.com/google/uuid"
	_ "github.com/lib/pq"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type Video struct {
	ID          uuid.UUID `json:"id"`
	Title       string    `json:"title"`
	Description string    `json:"description"`
	URL         string    `json:"url"`
	PublishedAt time.Time `json:"published_at"`
	Views       int       `json:"views"`
}

type VideoQueryRepository struct {
	postgresDB *sql.DB
	mongoDB    *mongo.Client
}

func NewVideoQueryRepository(postgresConnStr, mongoConnStr string) (*VideoQueryRepository, error) {
	// Initialize PostgreSQL connection
	pgDB, err := sql.Open("postgres", postgresConnStr)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to PostgreSQL: %v", err)
	}

	// Initialize MongoDB connection
	clientOptions := options.Client().ApplyURI(mongoConnStr)
	mongoClient, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to MongoDB: %v", err)
	}

	return &VideoQueryRepository{
		postgresDB: pgDB,
		mongoDB:    mongoClient,
	}, nil
}

// Fetch a video by its ID from PostgreSQL
func (repo *VideoQueryRepository) FetchVideoByID(id uuid.UUID) (*Video, error) {
	query := `SELECT id, title, description, url, published_at, views FROM videos WHERE id = $1`
	row := repo.postgresDB.QueryRow(query, id)

	var video Video
	err := row.Scan(&video.ID, &video.Title, &video.Description, &video.URL, &video.PublishedAt, &video.Views)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, fmt.Errorf("video with ID %v not found", id)
		}
		return nil, fmt.Errorf("failed to fetch video: %v", err)
	}

	return &video, nil
}

// Fetch all videos with pagination
func (repo *VideoQueryRepository) FetchAllVideos(page, limit int) ([]Video, error) {
	offset := (page - 1) * limit
	query := `SELECT id, title, description, url, published_at, views FROM videos LIMIT $1 OFFSET $2`

	rows, err := repo.postgresDB.Query(query, limit, offset)
	if err != nil {
		return nil, fmt.Errorf("failed to fetch videos: %v", err)
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		if err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.URL, &video.PublishedAt, &video.Views); err != nil {
			return nil, fmt.Errorf("failed to scan video: %v", err)
		}
		videos = append(videos, video)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("row iteration error: %v", err)
	}

	return videos, nil
}

// Fetch videos from MongoDB by criteria
func (repo *VideoQueryRepository) SearchVideosInMongo(title string) ([]Video, error) {
	collection := repo.mongoDB.Database("videodb").Collection("videos")

	filter := bson.M{"title": bson.M{"$regex": title, "$options": "i"}}
	cursor, err := collection.Find(context.TODO(), filter)
	if err != nil {
		return nil, fmt.Errorf("failed to search videos: %v", err)
	}
	defer cursor.Close(context.TODO())

	var videos []Video
	for cursor.Next(context.TODO()) {
		var video Video
		err := cursor.Decode(&video)
		if err != nil {
			return nil, fmt.Errorf("failed to decode video: %v", err)
		}
		videos = append(videos, video)
	}

	if err := cursor.Err(); err != nil {
		return nil, fmt.Errorf("cursor iteration error: %v", err)
	}

	return videos, nil
}

// Fetch video statistics by ID from MongoDB
func (repo *VideoQueryRepository) FetchVideoStats(id uuid.UUID) (map[string]interface{}, error) {
	collection := repo.mongoDB.Database("videodb").Collection("video_stats")

	filter := bson.M{"video_id": id}
	var result bson.M
	err := collection.FindOne(context.TODO(), filter).Decode(&result)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, fmt.Errorf("no statistics found for video ID %v", id)
		}
		return nil, fmt.Errorf("failed to fetch video statistics: %v", err)
	}

	return result, nil
}

// Count the total number of videos in PostgreSQL
func (repo *VideoQueryRepository) CountVideos() (int, error) {
	query := `SELECT COUNT(*) FROM videos`
	var count int
	err := repo.postgresDB.QueryRow(query).Scan(&count)
	if err != nil {
		return 0, fmt.Errorf("failed to count videos: %v", err)
	}

	return count, nil
}

// Fetch trending videos from PostgreSQL based on view count
func (repo *VideoQueryRepository) FetchTrendingVideos(limit int) ([]Video, error) {
	query := `SELECT id, title, description, url, published_at, views FROM videos ORDER BY views DESC LIMIT $1`
	rows, err := repo.postgresDB.Query(query, limit)
	if err != nil {
		return nil, fmt.Errorf("failed to fetch trending videos: %v", err)
	}
	defer rows.Close()

	var videos []Video
	for rows.Next() {
		var video Video
		if err := rows.Scan(&video.ID, &video.Title, &video.Description, &video.URL, &video.PublishedAt, &video.Views); err != nil {
			return nil, fmt.Errorf("failed to scan video: %v", err)
		}
		videos = append(videos, video)
	}

	return videos, nil
}

// Close the repository and all database connections
func (repo *VideoQueryRepository) Close() error {
	if err := repo.postgresDB.Close(); err != nil {
		return fmt.Errorf("failed to close PostgreSQL connection: %v", err)
	}

	if err := repo.mongoDB.Disconnect(context.TODO()); err != nil {
		return fmt.Errorf("failed to disconnect from MongoDB: %v", err)
	}

	return nil
}

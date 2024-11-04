package repositories

import (
	"database/sql"
	"fmt"
	"log"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/google/uuid"
	"github.com/jmoiron/sqlx"
)

type TranscodingRepository interface {
	CreateJob(input TranscodingJobInput) (string, error)
	GetJobStatus(jobID string) (TranscodingJob, error)
	UpdateJobStatus(jobID string, status string) error
	GetJobsByStatus(status string) ([]TranscodingJob, error)
}

type TranscodingJob struct {
	JobID        string    `db:"job_id"`
	VideoID      string    `db:"video_id"`
	InputFormat  string    `db:"input_format"`
	OutputFormat string    `db:"output_format"`
	Status       string    `db:"status"`
	CreatedAt    time.Time `db:"created_at"`
	UpdatedAt    time.Time `db:"updated_at"`
}

type TranscodingJobInput struct {
	VideoID      string
	InputFormat  string
	OutputFormat string
}

type TranscodingRepo struct {
	db *sqlx.DB
}

func NewTranscodingRepository(db *sqlx.DB) TranscodingRepository {
	return &TranscodingRepo{db: db}
}

func (r *TranscodingRepo) CreateJob(input TranscodingJobInput) (string, error) {
	jobID := uuid.New().String()
	query := `
        INSERT INTO transcoding_jobs (job_id, video_id, input_format, output_format, status, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    `
	_, err := r.db.Exec(query, jobID, input.VideoID, input.InputFormat, input.OutputFormat, "pending", time.Now(), time.Now())
	if err != nil {
		log.Printf("Error creating transcoding job: %v", err)
		return "", err
	}
	return jobID, nil
}

func (r *TranscodingRepo) GetJobStatus(jobID string) (TranscodingJob, error) {
	var job TranscodingJob
	query := `SELECT job_id, video_id, input_format, output_format, status, created_at, updated_at FROM transcoding_jobs WHERE job_id = ?`
	err := r.db.Get(&job, query, jobID)
	if err != nil {
		if err == sql.ErrNoRows {
			return TranscodingJob{}, fmt.Errorf("no job found with id: %s", jobID)
		}
		log.Printf("Error getting job status: %v", err)
		return TranscodingJob{}, err
	}
	return job, nil
}

func (r *TranscodingRepo) UpdateJobStatus(jobID string, status string) error {
	query := `UPDATE transcoding_jobs SET status = ?, updated_at = ? WHERE job_id = ?`
	_, err := r.db.Exec(query, status, time.Now(), jobID)
	if err != nil {
		log.Printf("Error updating job status: %v", err)
		return err
	}
	return nil
}

func (r *TranscodingRepo) GetJobsByStatus(status string) ([]TranscodingJob, error) {
	var jobs []TranscodingJob
	query := `SELECT job_id, video_id, input_format, output_format, status, created_at, updated_at FROM transcoding_jobs WHERE status = ?`
	err := r.db.Select(&jobs, query, status)
	if err != nil {
		log.Printf("Error fetching jobs by status: %v", err)
		return nil, err
	}
	return jobs, nil
}

// Database initialization and migrations

func InitDB(dataSourceName string) (*sqlx.DB, error) {
	db, err := sqlx.Connect("mysql", dataSourceName)
	if err != nil {
		log.Fatalln(err)
	}
	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(25)
	db.SetConnMaxLifetime(5 * time.Minute)

	err = runMigrations(db)
	if err != nil {
		return nil, err
	}

	return db, nil
}

func runMigrations(db *sqlx.DB) error {
	migrations := []string{
		`CREATE TABLE IF NOT EXISTS transcoding_jobs (
            job_id VARCHAR(36) NOT NULL,
            video_id VARCHAR(36) NOT NULL,
            input_format VARCHAR(50) NOT NULL,
            output_format VARCHAR(50) NOT NULL,
            status VARCHAR(50) NOT NULL,
            created_at DATETIME NOT NULL,
            updated_at DATETIME NOT NULL,
            PRIMARY KEY (job_id)
        )`,
	}

	for _, m := range migrations {
		_, err := db.Exec(m)
		if err != nil {
			return fmt.Errorf("migration failed: %v", err)
		}
	}
	return nil
}

// Helper function for transactional queries

func (r *TranscodingRepo) withTransaction(fn func(tx *sqlx.Tx) error) error {
	tx, err := r.db.Beginx()
	if err != nil {
		return err
	}

	err = fn(tx)
	if err != nil {
		tx.Rollback()
		return err
	}

	return tx.Commit()
}

// Job retry functionality for failed jobs

func (r *TranscodingRepo) RetryFailedJobs() error {
	jobs, err := r.GetJobsByStatus("failed")
	if err != nil {
		return err
	}

	for _, job := range jobs {
		err := r.withTransaction(func(tx *sqlx.Tx) error {
			query := `UPDATE transcoding_jobs SET status = ? WHERE job_id = ?`
			_, err := tx.Exec(query, "pending", job.JobID)
			if err != nil {
				return err
			}
			return nil
		})
		if err != nil {
			log.Printf("Failed to retry job %s: %v", job.JobID, err)
			return err
		}
	}

	return nil
}

// Cleanup old jobs

func (r *TranscodingRepo) CleanupOldJobs(daysOld int) error {
	cutoff := time.Now().AddDate(0, 0, -daysOld)
	query := `DELETE FROM transcoding_jobs WHERE status = 'completed' AND updated_at < ?`
	_, err := r.db.Exec(query, cutoff)
	if err != nil {
		log.Printf("Error cleaning up old jobs: %v", err)
		return err
	}
	return nil
}

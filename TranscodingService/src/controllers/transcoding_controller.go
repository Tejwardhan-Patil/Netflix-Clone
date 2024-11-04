package controllers

import (
	"TranscodingService/src/domain"
	"TranscodingService/src/services"
	"encoding/json"
	"log"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
)

// TranscodingController struct
type TranscodingController struct {
	TranscodingService services.TranscodingService
}

// NewTranscodingController initializes a new controller
func NewTranscodingController(service services.TranscodingService) *TranscodingController {
	return &TranscodingController{
		TranscodingService: service,
	}
}

// TranscodeVideo handles the video transcoding process
func (c *TranscodingController) TranscodeVideo(w http.ResponseWriter, r *http.Request) {
	var transcodingRequest domain.TranscodingRequest
	err := json.NewDecoder(r.Body).Decode(&transcodingRequest)
	if err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}

	result, err := c.TranscodingService.Transcode(transcodingRequest)
	if err != nil {
		log.Printf("Error during transcoding: %v", err)
		http.Error(w, "Transcoding failed", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(result)
}

// GetTranscodingStatus retrieves the status of an ongoing transcoding job
func (c *TranscodingController) GetTranscodingStatus(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	status, err := c.TranscodingService.GetStatus(jobID)
	if err != nil {
		log.Printf("Error fetching transcoding status: %v", err)
		http.Error(w, "Unable to fetch status", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(status)
}

// GetAllTranscodingJobs retrieves all transcoding jobs
func (c *TranscodingController) GetAllTranscodingJobs(w http.ResponseWriter, r *http.Request) {
	jobs, err := c.TranscodingService.GetAllJobs()
	if err != nil {
		log.Printf("Error fetching all transcoding jobs: %v", err)
		http.Error(w, "Unable to fetch transcoding jobs", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(jobs)
}

// CancelTranscodingJob cancels a transcoding job by its ID
func (c *TranscodingController) CancelTranscodingJob(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	err := c.TranscodingService.CancelJob(jobID)
	if err != nil {
		log.Printf("Error canceling transcoding job: %v", err)
		http.Error(w, "Failed to cancel transcoding job", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}

// GetJobLogs retrieves logs for a transcoding job
func (c *TranscodingController) GetJobLogs(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	logs, err := c.TranscodingService.GetJobLogs(jobID)
	if err != nil {
		log.Printf("Error fetching logs for job: %v", err)
		http.Error(w, "Unable to fetch logs", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(logs)
}

// ResubmitFailedJob retries a failed transcoding job
func (c *TranscodingController) ResubmitFailedJob(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	err := c.TranscodingService.ResubmitJob(jobID)
	if err != nil {
		log.Printf("Error resubmitting job: %v", err)
		http.Error(w, "Failed to resubmit job", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// TranscodingControllerRoutes registers the routes for the controller
func (c *TranscodingController) TranscodingControllerRoutes(router *mux.Router) {
	router.HandleFunc("/transcode", c.TranscodeVideo).Methods("POST")
	router.HandleFunc("/transcode/status/{jobID}", c.GetTranscodingStatus).Methods("GET")
	router.HandleFunc("/transcode/jobs", c.GetAllTranscodingJobs).Methods("GET")
	router.HandleFunc("/transcode/cancel/{jobID}", c.CancelTranscodingJob).Methods("DELETE")
	router.HandleFunc("/transcode/logs/{jobID}", c.GetJobLogs).Methods("GET")
	router.HandleFunc("/transcode/resubmit/{jobID}", c.ResubmitFailedJob).Methods("POST")
}

// GetVideoFormats retrieves supported video formats for transcoding
func (c *TranscodingController) GetVideoFormats(w http.ResponseWriter, r *http.Request) {
	formats, err := c.TranscodingService.GetSupportedFormats()
	if err != nil {
		log.Printf("Error fetching video formats: %v", err)
		http.Error(w, "Unable to fetch formats", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(formats)
}

// ChangePriority alters the priority of a transcoding job
func (c *TranscodingController) ChangePriority(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]
	priority, err := strconv.Atoi(vars["priority"])
	if err != nil {
		log.Printf("Invalid priority: %v", err)
		http.Error(w, "Invalid priority value", http.StatusBadRequest)
		return
	}

	err = c.TranscodingService.UpdatePriority(jobID, priority)
	if err != nil {
		log.Printf("Error updating priority: %v", err)
		http.Error(w, "Failed to update priority", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// HandleWebhook handles webhook notifications for transcoding completion
func (c *TranscodingController) HandleWebhook(w http.ResponseWriter, r *http.Request) {
	var notification domain.TranscodingNotification
	err := json.NewDecoder(r.Body).Decode(&notification)
	if err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}

	err = c.TranscodingService.ProcessWebhook(notification)
	if err != nil {
		log.Printf("Error processing webhook: %v", err)
		http.Error(w, "Webhook processing failed", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// PauseJob pauses an active transcoding job
func (c *TranscodingController) PauseJob(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	err := c.TranscodingService.PauseJob(jobID)
	if err != nil {
		log.Printf("Error pausing job: %v", err)
		http.Error(w, "Failed to pause job", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// ResumeJob resumes a paused transcoding job
func (c *TranscodingController) ResumeJob(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	jobID := vars["jobID"]

	err := c.TranscodingService.ResumeJob(jobID)
	if err != nil {
		log.Printf("Error resuming job: %v", err)
		http.Error(w, "Failed to resume job", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// HealthCheck verifies the health of the transcoding service
func (c *TranscodingController) HealthCheck(w http.ResponseWriter, r *http.Request) {
	status := c.TranscodingService.CheckHealth()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(status)
}

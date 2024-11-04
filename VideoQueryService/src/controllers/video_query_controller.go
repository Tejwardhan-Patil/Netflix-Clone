package controllers

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"VideoQueryService/src/repositories"
	"VideoQueryService/src/services"
	"log"

	"github.com/gorilla/mux"
)

// VideoQueryController struct to handle video queries
type VideoQueryController struct {
	VideoQueryService services.VideoQueryService
}

// NewVideoQueryController creates a new instance of VideoQueryController
func NewVideoQueryController(service services.VideoQueryService) *VideoQueryController {
	return &VideoQueryController{
		VideoQueryService: service,
	}
}

// GetVideoByID handles the request to fetch video by its ID
func (controller *VideoQueryController) GetVideoByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	videoID := vars["id"]

	video, err := controller.VideoQueryService.FetchVideoByID(videoID)
	if err != nil {
		http.Error(w, fmt.Sprintf("Error fetching video: %s", err.Error()), http.StatusInternalServerError)
		return
	}

	response, _ := json.Marshal(video)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(response)
}

// GetVideosByCategory handles the request to fetch videos by category
func (controller *VideoQueryController) GetVideosByCategory(w http.ResponseWriter, r *http.Request) {
	category := r.URL.Query().Get("category")

	videos, err := controller.VideoQueryService.FetchVideosByCategory(category)
	if err != nil {
		http.Error(w, fmt.Sprintf("Error fetching videos: %s", err.Error()), http.StatusInternalServerError)
		return
	}

	response, _ := json.Marshal(videos)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(response)
}

// GetVideosByDateRange handles fetching videos within a date range
func (controller *VideoQueryController) GetVideosByDateRange(w http.ResponseWriter, r *http.Request) {
	startDate := r.URL.Query().Get("start_date")
	endDate := r.URL.Query().Get("end_date")

	videos, err := controller.VideoQueryService.FetchVideosByDateRange(startDate, endDate)
	if err != nil {
		http.Error(w, fmt.Sprintf("Error fetching videos: %s", err.Error()), http.StatusInternalServerError)
		return
	}

	response, _ := json.Marshal(videos)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(response)
}

// GetTopRatedVideos handles fetching top-rated videos
func (controller *VideoQueryController) GetTopRatedVideos(w http.ResponseWriter, r *http.Request) {
	limitStr := r.URL.Query().Get("limit")
	limit, err := strconv.Atoi(limitStr)
	if err != nil || limit <= 0 {
		http.Error(w, "Invalid limit parameter", http.StatusBadRequest)
		return
	}

	videos, err := controller.VideoQueryService.FetchTopRatedVideos(limit)
	if err != nil {
		http.Error(w, fmt.Sprintf("Error fetching videos: %s", err.Error()), http.StatusInternalServerError)
		return
	}

	response, _ := json.Marshal(videos)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(response)
}

// RegisterRoutes registers all video query routes with the router
func RegisterRoutes(router *mux.Router, videoQueryService services.VideoQueryService) {
	controller := NewVideoQueryController(videoQueryService)

	router.HandleFunc("/videos/{id}", controller.GetVideoByID).Methods(http.MethodGet)
	router.HandleFunc("/videos/category", controller.GetVideosByCategory).Methods(http.MethodGet)
	router.HandleFunc("/videos/date-range", controller.GetVideosByDateRange).Methods(http.MethodGet)
	router.HandleFunc("/videos/top-rated", controller.GetTopRatedVideos).Methods(http.MethodGet)
}

func main() {
	router := mux.NewRouter()
	videoRepo := repositories.NewVideoQueryRepository()
	videoService := services.NewVideoQueryService(videoRepo)

	RegisterRoutes(router, videoService)

	fmt.Println("Starting server on port 8080...")
	log.Fatal(http.ListenAndServe(":8080", router))
}

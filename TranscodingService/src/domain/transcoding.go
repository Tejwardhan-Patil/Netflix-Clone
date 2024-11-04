package domain

import (
	"errors"
	"fmt"
	"os"
	"os/exec"
	"strings"
)

// TranscodingStatus represents the current state of the transcoding process
type TranscodingStatus int

const (
	Queued TranscodingStatus = iota
	InProgress
	Completed
	Failed
)

// VideoFormat represents different video formats supported for transcoding
type VideoFormat string

const (
	MP4 VideoFormat = "mp4"
	MKV VideoFormat = "mkv"
	AVI VideoFormat = "avi"
	MOV VideoFormat = "mov"
)

// Resolution defines different video resolutions
type Resolution string

const (
	SD  Resolution = "480p"
	HD  Resolution = "720p"
	FHD Resolution = "1080p"
	UHD Resolution = "2160p"
)

// TranscodingRequest represents a transcoding job request
type TranscodingRequest struct {
	InputFile        string
	OutputFile       string
	TargetFormat     VideoFormat
	TargetResolution Resolution
	Status           TranscodingStatus
	Progress         int // in percentage
	ErrorMessage     string
}

// Validate checks if the request has valid parameters
func (r *TranscodingRequest) Validate() error {
	if r.InputFile == "" {
		return errors.New("input file cannot be empty")
	}

	if r.OutputFile == "" {
		return errors.New("output file cannot be empty")
	}

	if !isSupportedFormat(r.TargetFormat) {
		return errors.New("unsupported video format: " + string(r.TargetFormat))
	}

	if !isSupportedResolution(r.TargetResolution) {
		return errors.New("unsupported video resolution: " + string(r.TargetResolution))
	}

	if _, err := os.Stat(r.InputFile); os.IsNotExist(err) {
		return fmt.Errorf("input file does not exist: %s", r.InputFile)
	}

	return nil
}

// StartTranscoding initializes and starts the transcoding process
func (r *TranscodingRequest) StartTranscoding() error {
	r.Status = InProgress

	err := r.Validate()
	if err != nil {
		r.Status = Failed
		r.ErrorMessage = err.Error()
		return err
	}

	command := buildTranscodingCommand(r.InputFile, r.OutputFile, r.TargetFormat, r.TargetResolution)

	if err := runCommand(command); err != nil {
		r.Status = Failed
		r.ErrorMessage = fmt.Sprintf("failed to transcode video: %v", err)
		return err
	}

	r.Status = Completed
	r.Progress = 100
	return nil
}

// buildTranscodingCommand constructs the ffmpeg command to execute transcoding
func buildTranscodingCommand(inputFile, outputFile string, format VideoFormat, resolution Resolution) string {
	var resolutionOption string

	switch resolution {
	case SD:
		resolutionOption = "640x480"
	case HD:
		resolutionOption = "1280x720"
	case FHD:
		resolutionOption = "1920x1080"
	case UHD:
		resolutionOption = "3840x2160"
	default:
		resolutionOption = "1280x720" // Default to HD
	}

	return fmt.Sprintf("ffmpeg -i %s -s %s -c:v libx264 -preset fast -crf 22 %s.%s", inputFile, resolutionOption, outputFile, format)
}

// runCommand executes the ffmpeg command
func runCommand(command string) error {
	cmdParts := strings.Split(command, " ")
	cmd := exec.Command(cmdParts[0], cmdParts[1:]...)

	output, err := cmd.CombinedOutput()
	if err != nil {
		return fmt.Errorf("error executing command: %v, output: %s", err, output)
	}

	return nil
}

// isSupportedFormat checks if the provided format is supported
func isSupportedFormat(format VideoFormat) bool {
	switch format {
	case MP4, MKV, AVI, MOV:
		return true
	default:
		return false
	}
}

// isSupportedResolution checks if the provided resolution is supported
func isSupportedResolution(resolution Resolution) bool {
	switch resolution {
	case SD, HD, FHD, UHD:
		return true
	default:
		return false
	}
}

// TranscodingService handles multiple transcoding requests
type TranscodingService struct {
	Queue []*TranscodingRequest
}

// NewTranscodingService creates a new transcoding service
func NewTranscodingService() *TranscodingService {
	return &TranscodingService{
		Queue: make([]*TranscodingRequest, 0),
	}
}

// AddRequest adds a new transcoding request to the queue
func (s *TranscodingService) AddRequest(inputFile, outputFile string, format VideoFormat, resolution Resolution) (*TranscodingRequest, error) {
	request := &TranscodingRequest{
		InputFile:        inputFile,
		OutputFile:       outputFile,
		TargetFormat:     format,
		TargetResolution: resolution,
		Status:           Queued,
		Progress:         0,
	}

	err := request.Validate()
	if err != nil {
		return nil, err
	}

	s.Queue = append(s.Queue, request)
	return request, nil
}

// ProcessQueue starts processing all requests in the queue
func (s *TranscodingService) ProcessQueue() {
	for _, request := range s.Queue {
		if request.Status == Queued {
			fmt.Printf("Starting transcoding for file: %s\n", request.InputFile)
			err := request.StartTranscoding()
			if err != nil {
				fmt.Printf("Error: %s\n", request.ErrorMessage)
			} else {
				fmt.Printf("Completed transcoding: %s\n", request.OutputFile)
			}
		}
	}
}

// RemoveCompleted removes completed requests from the queue
func (s *TranscodingService) RemoveCompleted() {
	var newQueue []*TranscodingRequest
	for _, request := range s.Queue {
		if request.Status != Completed {
			newQueue = append(newQueue, request)
		}
	}
	s.Queue = newQueue
}

// GetQueue returns the current list of requests
func (s *TranscodingService) GetQueue() []*TranscodingRequest {
	return s.Queue
}

// FileSize gets the size of the transcoded file
func FileSize(filePath string) (int64, error) {
	fileInfo, err := os.Stat(filePath)
	if err != nil {
		return 0, fmt.Errorf("could not retrieve file info: %v", err)
	}
	return fileInfo.Size(), nil
}

// ConvertBytesToMB converts bytes to megabytes
func ConvertBytesToMB(bytes int64) float64 {
	return float64(bytes) / (1024 * 1024)
}

// LogFileSize logs the file size of a transcoded video
func LogFileSize(filePath string) error {
	size, err := FileSize(filePath)
	if err != nil {
		return err
	}

	fmt.Printf("File size of %s: %.2f MB\n", filePath, ConvertBytesToMB(size))
	return nil
}

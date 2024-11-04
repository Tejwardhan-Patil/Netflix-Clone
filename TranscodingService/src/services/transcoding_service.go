package services

import (
	"fmt"
	"log"
	"os/exec"
	"path/filepath"
	"sync"
	"time"
)

type TranscodingService struct {
	taskQueue     chan *TranscodingTask
	activeTasks   map[string]*TranscodingTask
	taskMutex     sync.Mutex
	maxConcurrent int
}

type TranscodingTask struct {
	ID         string
	InputFile  string
	OutputFile string
	Status     string
	Progress   float64
	StartedAt  time.Time
	FinishedAt time.Time
	Error      error
}

// NewTranscodingService creates a new TranscodingService
func NewTranscodingService(queueSize, maxConcurrent int) *TranscodingService {
	return &TranscodingService{
		taskQueue:     make(chan *TranscodingTask, queueSize),
		activeTasks:   make(map[string]*TranscodingTask),
		maxConcurrent: maxConcurrent,
	}
}

// StartQueue starts processing the task queue
func (s *TranscodingService) StartQueue() {
	for i := 0; i < s.maxConcurrent; i++ {
		go s.worker()
	}
}

// AddTask adds a new transcoding task to the queue
func (s *TranscodingService) AddTask(task *TranscodingTask) {
	s.taskQueue <- task
}

// worker processes tasks from the queue
func (s *TranscodingService) worker() {
	for task := range s.taskQueue {
		s.startTask(task)
		s.processTask(task)
		s.completeTask(task)
	}
}

// startTask updates task state and adds it to activeTasks
func (s *TranscodingService) startTask(task *TranscodingTask) {
	task.StartedAt = time.Now()
	task.Status = "Started"
	s.taskMutex.Lock()
	s.activeTasks[task.ID] = task
	s.taskMutex.Unlock()
	log.Printf("Task %s started.", task.ID)
}

// completeTask marks the task as finished and removes it from activeTasks
func (s *TranscodingService) completeTask(task *TranscodingTask) {
	task.FinishedAt = time.Now()
	s.taskMutex.Lock()
	delete(s.activeTasks, task.ID)
	s.taskMutex.Unlock()
	if task.Error != nil {
		log.Printf("Task %s completed with error: %v", task.ID, task.Error)
	} else {
		log.Printf("Task %s completed successfully.", task.ID)
	}
}

// GetActiveTasks returns a list of active transcoding tasks
func (s *TranscodingService) GetActiveTasks() []*TranscodingTask {
	s.taskMutex.Lock()
	defer s.taskMutex.Unlock()
	tasks := make([]*TranscodingTask, 0, len(s.activeTasks))
	for _, task := range s.activeTasks {
		tasks = append(tasks, task)
	}
	return tasks
}

// GetTaskByID retrieves a task by its ID
func (s *TranscodingService) GetTaskByID(taskID string) (*TranscodingTask, error) {
	s.taskMutex.Lock()
	defer s.taskMutex.Unlock()
	task, exists := s.activeTasks[taskID]
	if !exists {
		return nil, fmt.Errorf("task %s not found", taskID)
	}
	return task, nil
}

// processTask runs the transcoding process for a task
func (s *TranscodingService) processTask(task *TranscodingTask) {
	// Simulate transcoding with a sleep
	time.Sleep(5 * time.Second)

	inputFilePath := task.InputFile
	outputFilePath := task.OutputFile

	err := s.runTranscoding(inputFilePath, outputFilePath)
	if err != nil {
		task.Error = err
		task.Status = "Failed"
	} else {
		task.Status = "Completed"
		task.Progress = 100
	}
}

// runTranscoding performs the actual transcoding
func (s *TranscodingService) runTranscoding(inputFile, outputFile string) error {
	// Command that uses ffmpeg for video transcoding
	cmd := exec.Command("ffmpeg", "-i", inputFile, "-codec:v", "libx264", outputFile)

	// Run the command and capture output
	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Printf("Transcoding error: %v\nOutput: %s", err, string(output))
		return fmt.Errorf("transcoding failed: %v", err)
	}

	log.Printf("Transcoding successful for file: %s", inputFile)
	return nil
}

// SaveTaskResults saves the transcoding results to a file or database
func (s *TranscodingService) SaveTaskResults(task *TranscodingTask) error {
	// Simulate saving the results to a file or database
	resultPath := filepath.Join("transcoding_results", task.ID+".json")
	err := writeToFile(resultPath, fmt.Sprintf("Task ID: %s, Status: %s", task.ID, task.Status))
	if err != nil {
		return err
	}
	log.Printf("Results saved for task: %s", task.ID)
	return nil
}

// writeToFile saves the transcoding results to a file
func writeToFile(filePath, content string) error {
	file, err := exec.Command("touch", filePath).Output()
	if err != nil {
		log.Printf("Error creating file: %v", err)
		return err
	}
	err = exec.Command("echo", content).Run()
	if err != nil {
		log.Printf("Error writing to file: %v", err)
		return err
	}
	log.Printf("File written successfully: %s", string(file))
	return nil
}

// CancelTask cancels a transcoding task by ID
func (s *TranscodingService) CancelTask(taskID string) error {
	s.taskMutex.Lock()
	defer s.taskMutex.Unlock()

	task, exists := s.activeTasks[taskID]
	if !exists {
		return fmt.Errorf("task %s not found", taskID)
	}

	// Signal a running process to stop
	task.Status = "Cancelled"
	log.Printf("Task %s has been cancelled.", task.ID)
	return nil
}

// RetryTask retries a failed task by ID
func (s *TranscodingService) RetryTask(taskID string) error {
	task, err := s.GetTaskByID(taskID)
	if err != nil {
		return err
	}
	if task.Status != "Failed" {
		return fmt.Errorf("task %s is not in a failed state", taskID)
	}

	log.Printf("Retrying task %s", task.ID)
	go s.AddTask(task)
	return nil
}

// MonitorProgress simulates a real-time task progress update
func (s *TranscodingService) MonitorProgress(taskID string) {
	for {
		task, err := s.GetTaskByID(taskID)
		if err != nil || task.Status == "Completed" || task.Status == "Failed" {
			break
		}

		s.updateProgress(task)
		time.Sleep(2 * time.Second)
	}
}

// updateProgress updates the task progress periodically
func (s *TranscodingService) updateProgress(task *TranscodingTask) {
	task.Progress += 10.0
	if task.Progress > 100.0 {
		task.Progress = 100.0
	}
	log.Printf("Progress updated for task %s: %.2f%%", task.ID, task.Progress)
}

package com.netflixclone.videocommandservice.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.netflixclone.videocommandservice.services.VideoCommandService
import com.netflixclone.videocommandservice.commands.AddVideoCommand
import com.netflixclone.videocommandservice.commands.UpdateVideoCommand
import javax.validation.Valid

@RestController
@RequestMapping("/api/command/videos")
class VideoCommandController(private val videoCommandService: VideoCommandService) {

    // Endpoint to add a new video
    @PostMapping("/add")
    fun addVideo(@Valid @RequestBody command: AddVideoCommand): ResponseEntity<String> {
        return try {
            videoCommandService.handleAddVideo(command)
            ResponseEntity("Video added successfully", HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to update an existing video
    @PutMapping("/update/{id}")
    fun updateVideo(@PathVariable id: Long, @Valid @RequestBody command: UpdateVideoCommand): ResponseEntity<String> {
        return try {
            videoCommandService.handleUpdateVideo(id, command)
            ResponseEntity("Video updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to delete a video
    @DeleteMapping("/delete/{id}")
    fun deleteVideo(@PathVariable id: Long): ResponseEntity<String> {
        return try {
            videoCommandService.handleDeleteVideo(id)
            ResponseEntity("Video deleted successfully", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to handle video metadata updates (such as description, tags)
    @PutMapping("/update-metadata/{id}")
    fun updateMetadata(@PathVariable id: Long, @Valid @RequestBody metadata: Map<String, String>): ResponseEntity<String> {
        return try {
            videoCommandService.handleUpdateMetadata(id, metadata)
            ResponseEntity("Metadata updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to handle video status change (publishing/unpublishing a video)
    @PutMapping("/update-status/{id}")
    fun updateVideoStatus(@PathVariable id: Long, @RequestParam status: String): ResponseEntity<String> {
        return try {
            videoCommandService.handleUpdateStatus(id, status)
            ResponseEntity("Video status updated to $status", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to handle batch video processing (for bulk updates)
    @PostMapping("/batch-process")
    fun batchProcessVideos(@Valid @RequestBody videoCommands: List<AddVideoCommand>): ResponseEntity<String> {
        return try {
            videoCommandService.handleBatchProcessing(videoCommands)
            ResponseEntity("Batch processing successful", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to update video visibility (public/private)
    @PutMapping("/update-visibility/{id}")
    fun updateVisibility(@PathVariable id: Long, @RequestParam visibility: String): ResponseEntity<String> {
        return try {
            videoCommandService.handleUpdateVisibility(id, visibility)
            ResponseEntity("Video visibility updated to $visibility", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to process video transcoding
    @PostMapping("/transcode/{id}")
    fun transcodeVideo(@PathVariable id: Long): ResponseEntity<String> {
        return try {
            videoCommandService.handleTranscodeVideo(id)
            ResponseEntity("Video transcoding initiated", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to handle video tagging
    @PutMapping("/add-tags/{id}")
    fun addTagsToVideo(@PathVariable id: Long, @Valid @RequestBody tags: List<String>): ResponseEntity<String> {
        return try {
            videoCommandService.handleAddTags(id, tags)
            ResponseEntity("Tags added to video", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to remove tags from a video
    @PutMapping("/remove-tags/{id}")
    fun removeTagsFromVideo(@PathVariable id: Long, @Valid @RequestBody tags: List<String>): ResponseEntity<String> {
        return try {
            videoCommandService.handleRemoveTags(id, tags)
            ResponseEntity("Tags removed from video", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Endpoint to update video thumbnail
    @PutMapping("/update-thumbnail/{id}")
    fun updateThumbnail(@PathVariable id: Long, @RequestParam thumbnailUrl: String): ResponseEntity<String> {
        return try {
            videoCommandService.handleUpdateThumbnail(id, thumbnailUrl)
            ResponseEntity("Thumbnail updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
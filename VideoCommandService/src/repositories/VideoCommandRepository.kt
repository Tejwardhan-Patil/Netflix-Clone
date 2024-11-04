package com.website.videocommandservice.repositories

import com.website.videocommandservice.domain.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.transaction.Transactional

@Repository
interface VideoCommandRepository : JpaRepository<Video, UUID> {
    
    /**
     * Find a video by its unique identifier
     * 
     * @param id UUID of the video
     * @return Video entity if found, otherwise null
     */
    fun findById(id: UUID): Video?

    /**
     * Save a new video or update an existing video in the database
     * 
     * @param video Video entity to save or update
     * @return Saved Video entity
     */
    @Transactional
    fun save(video: Video): Video

    /**
     * Delete a video by its unique identifier
     * 
     * @param id UUID of the video to delete
     */
    @Transactional
    fun deleteById(id: UUID)
    
    /**
     * Update the title of a video by its UUID
     * 
     * @param id UUID of the video
     * @param newTitle New title of the video
     */
    @Transactional
    fun updateTitle(id: UUID, newTitle: String)

    /**
     * Update the description of a video
     * 
     * @param id UUID of the video
     * @param newDescription New description of the video
     */
    @Transactional
    fun updateDescription(id: UUID, newDescription: String)
}

package com.website.videocommandservice.domain

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "videos")
data class Video(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @Column(name = "upload_time", nullable = false)
    var uploadTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "uploader_id", nullable = false)
    var uploaderId: UUID,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
)

package com.website.videocommandservice.services

import com.website.videocommandservice.domain.Video
import com.website.videocommandservice.repositories.VideoCommandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class VideoCommandService @Autowired constructor(
    private val videoCommandRepository: VideoCommandRepository
) {

    /**
     * Add a new video to the repository
     * 
     * @param title Title of the video
     * @param description Description of the video
     * @param uploaderId UUID of the uploader
     * @return Saved video entity
     */
    fun addVideo(title: String, description: String, uploaderId: UUID): Video {
        val video = Video(
            title = title,
            description = description,
            uploaderId = uploaderId
        )
        return videoCommandRepository.save(video)
    }

    /**
     * Update the title of an existing video
     * 
     * @param videoId UUID of the video to update
     * @param newTitle New title for the video
     */
    fun updateVideoTitle(videoId: UUID, newTitle: String) {
        val video = videoCommandRepository.findById(videoId)
        if (video != null) {
            video.title = newTitle
            videoCommandRepository.save(video)
        } else {
            throw IllegalArgumentException("Video with ID $videoId not found.")
        }
    }

    /**
     * Update the description of an existing video
     * 
     * @param videoId UUID of the video to update
     * @param newDescription New description for the video
     */
    fun updateVideoDescription(videoId: UUID, newDescription: String) {
        val video = videoCommandRepository.findById(videoId)
        if (video != null) {
            video.description = newDescription
            videoCommandRepository.save(video)
        } else {
            throw IllegalArgumentException("Video with ID $videoId not found.")
        }
    }

    /**
     * Delete a video by its UUID
     * 
     * @param videoId UUID of the video to delete
     */
    fun deleteVideo(videoId: UUID) {
        videoCommandRepository.deleteById(videoId)
    }
}

package com.website.videocommandservice.controllers

import com.website.videocommandservice.domain.Video
import com.website.videocommandservice.services.VideoCommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/commands/videos")
class VideoCommandController @Autowired constructor(
    private val videoCommandService: VideoCommandService
) {

    @PostMapping("/add")
    fun addVideo(
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam uploaderId: UUID
    ): ResponseEntity<Video> {
        val video = videoCommandService.addVideo(title, description, uploaderId)
        return ResponseEntity.ok(video)
    }

    @PutMapping("/update/title/{id}")
    fun updateVideoTitle(
        @PathVariable id: UUID,
        @RequestParam newTitle: String
    ): ResponseEntity<Void> {
        videoCommandService.updateVideoTitle(id, newTitle)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/update/description/{id}")
    fun updateVideoDescription(
        @PathVariable id: UUID,
        @RequestParam newDescription: String
    ): ResponseEntity<Void> {
        videoCommandService.updateVideoDescription(id, newDescription)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/delete/{id}")
    fun deleteVideo(@PathVariable id: UUID): ResponseEntity<Void> {
        videoCommandService.deleteVideo(id)
        return ResponseEntity.noContent().build()
    }
}
package com.netflixclone.videoservice.services

import com.netflixclone.videoservice.commands.AddVideoCommand
import com.netflixclone.videoservice.commands.UpdateVideoCommand
import com.netflixclone.videoservice.commands.DeleteVideoCommand
import com.netflixclone.videoservice.domain.Video
import com.netflixclone.videoservice.repositories.VideoCommandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VideoCommandService @Autowired constructor(
    private val videoCommandRepository: VideoCommandRepository
) {

    @Transactional
    fun addVideo(command: AddVideoCommand): Video {
        val video = Video(
            id = UUID.randomUUID(),
            title = command.title,
            description = command.description,
            genre = command.genre,
            releaseDate = command.releaseDate,
            duration = command.duration,
            url = command.url
        )
        videoCommandRepository.save(video)
        return video
    }

    @Transactional
    fun updateVideo(command: UpdateVideoCommand): Video {
        val video = videoCommandRepository.findById(command.id)
            ?: throw IllegalArgumentException("Video not found with id: ${command.id}")
        
        video.title = command.title
        video.description = command.description
        video.genre = command.genre
        video.releaseDate = command.releaseDate
        video.duration = command.duration
        video.url = command.url
        
        videoCommandRepository.save(video)
        return video
    }

    @Transactional
    fun deleteVideo(command: DeleteVideoCommand) {
        val video = videoCommandRepository.findById(command.id)
            ?: throw IllegalArgumentException("Video not found with id: ${command.id}")
        
        videoCommandRepository.delete(video)
    }
}

// VideoCommandRepository.kt
package com.netflixclone.videoservice.repositories

import com.netflixclone.videoservice.domain.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VideoCommandRepository : JpaRepository<Video, UUID> {
}

// Video.kt
package com.netflixclone.videoservice.domain

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "videos")
data class Video(
    @Id
    val id: UUID,
    var title: String,
    var description: String,
    var genre: String,
    var releaseDate: LocalDateTime,
    var duration: Int,
    var url: String
)

// AddVideoCommand.kt
package com.netflixclone.videoservice.commands

import java.time.LocalDateTime

data class AddVideoCommand(
    val title: String,
    val description: String,
    val genre: String,
    val releaseDate: LocalDateTime,
    val duration: Int,
    val url: String
)

// UpdateVideoCommand.kt
package com.netflixclone.videoservice.commands

import java.time.LocalDateTime
import java.util.UUID

data class UpdateVideoCommand(
    val id: UUID,
    val title: String,
    val description: String,
    val genre: String,
    val releaseDate: LocalDateTime,
    val duration: Int,
    val url: String
)

// DeleteVideoCommand.kt
package com.netflixclone.videoservice.commands

import java.util.UUID

data class DeleteVideoCommand(
    val id: UUID
)

// VideoCommandController.kt
package com.netflixclone.videoservice.controllers

import com.netflixclone.videoservice.commands.AddVideoCommand
import com.netflixclone.videoservice.commands.UpdateVideoCommand
import com.netflixclone.videoservice.commands.DeleteVideoCommand
import com.netflixclone.videoservice.domain.Video
import com.netflixclone.videoservice.services.VideoCommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/videos")
class VideoCommandController @Autowired constructor(
    private val videoCommandService: VideoCommandService
) {

    @PostMapping
    fun addVideo(@RequestBody command: AddVideoCommand): ResponseEntity<Video> {
        val video = videoCommandService.addVideo(command)
        return ResponseEntity.ok(video)
    }

    @PutMapping("/{id}")
    fun updateVideo(
        @PathVariable id: UUID,
        @RequestBody command: UpdateVideoCommand
    ): ResponseEntity<Video> {
        val video = videoCommandService.updateVideo(command.copy(id = id))
        return ResponseEntity.ok(video)
    }

    @DeleteMapping("/{id}")
    fun deleteVideo(@PathVariable id: UUID): ResponseEntity<Void> {
        videoCommandService.deleteVideo(DeleteVideoCommand(id))
        return ResponseEntity.noContent().build()
    }
}
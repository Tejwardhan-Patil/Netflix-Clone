package com.netflixclone.videoservice.commands

import com.netflixclone.videoservice.domain.Video
import com.netflixclone.videoservice.services.VideoCommandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AddVideoRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Description is required")
    val description: String,

    @field:NotBlank(message = "URL is required")
    val videoUrl: String,

    @field:NotNull(message = "Duration is required")
    val duration: Int, // duration in seconds

    @field:NotNull(message = "Upload date is required")
    val uploadDate: LocalDateTime,

    val tags: List<String>? = null,
    val isPublic: Boolean = true
)

@Component
class AddVideoCommand @Autowired constructor(
    private val videoCommandService: VideoCommandService
) {

    fun handle(request: AddVideoRequest): AddVideoResponse {
        // Validate input 

        val video = Video(
            title = request.title,
            description = request.description,
            videoUrl = request.videoUrl,
            duration = request.duration,
            uploadDate = request.uploadDate,
            tags = request.tags ?: listOf(),
            isPublic = request.isPublic
        )

        // Save video using the command service
        videoCommandService.addVideo(video)

        // Return response
        return AddVideoResponse(
            success = true,
            message = "Video added successfully",
            videoId = video.id ?: throw RuntimeException("Video ID should not be null after saving")
        )
    }
}

data class AddVideoResponse(
    val success: Boolean,
    val message: String,
    val videoId: Long
)

// Domain model class 
data class Video(
    var id: Long? = null,
    val title: String,
    val description: String,
    val videoUrl: String,
    val duration: Int,
    val uploadDate: LocalDateTime,
    val tags: List<String>,
    val isPublic: Boolean
)

// VideoCommandService class to handle command-related logic
package com.netflixclone.videoservice.services

import com.netflixclone.videoservice.domain.Video
import com.netflixclone.videoservice.repositories.VideoCommandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VideoCommandService @Autowired constructor(
    private val videoCommandRepository: VideoCommandRepository
) {

    @Transactional
    fun addVideo(video: Video) {
        videoCommandRepository.save(video)
    }
}

// VideoCommandRepository interface for database operations
package com.netflixclone.videoservice.repositories

import com.netflixclone.videoservice.domain.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoCommandRepository : JpaRepository<Video, Long>

// Spring Boot configuration
spring:
  datasource:
    url: jdbc:mysql:
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5Dialect

// Controller to handle API requests for video commands
package com.netflixclone.videoservice.controllers

import com.netflixclone.videoservice.commands.AddVideoCommand
import com.netflixclone.videoservice.commands.AddVideoRequest
import com.netflixclone.videoservice.commands.AddVideoResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/video/commands")
class VideoCommandController @Autowired constructor(
    private val addVideoCommand: AddVideoCommand
) {

    @PostMapping("/add")
    fun addVideo(@RequestBody request: AddVideoRequest): ResponseEntity<AddVideoResponse> {
        val response = addVideoCommand.handle(request)
        return ResponseEntity.ok(response)
    }
}

// Testing the AddVideoCommand (integration test)
package com.netflixclone.videoservice.commands

import com.netflixclone.videoservice.services.VideoCommandService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class AddVideoCommandTest @Autowired constructor(
    private val videoCommandService: VideoCommandService
) {

    @Test
    fun `test successful video addition`() {
        // Mock the input
        val request = AddVideoRequest(
            title = "Sample Video",
            description = "Sample Description",
            videoUrl = "http://website.com/sample_video.mp4",
            duration = 3600,
            uploadDate = LocalDateTime.now()
        )

        // Create a mock command
        val addVideoCommand = AddVideoCommand(videoCommandService)

        // Call handle function
        val response = addVideoCommand.handle(request)

        // Verify the result
        assertEquals(true, response.success)
        assertEquals("Video added successfully", response.message)
        verify(videoCommandService, times(1)).addVideo(any())
    }
}
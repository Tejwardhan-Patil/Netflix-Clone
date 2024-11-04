package com.netflixclone.user.repositories

import com.netflixclone.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    
    // Find a user by their email
    fun findByEmail(email: String): Optional<User>
    
    // Check if a user exists by their email
    fun existsByEmail(email: String): Boolean
    
    // Find users by role
    fun findAllByRole(role: String): List<User>
    
    // Find a user by their username
    fun findByUsername(username: String): Optional<User>
    
    // Check if a user exists by their username
    fun existsByUsername(username: String): Boolean
    
    // Find all users by their status
    fun findAllByStatus(status: String): List<User>
}

package com.netflixclone.user.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val role: String = "USER",

    @Column(nullable = false)
    val status: String = "ACTIVE"
)

package com.netflixclone.user.services

import com.netflixclone.user.domain.User
import com.netflixclone.user.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    // Create or save a user
    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    // Find a user by ID
    fun findUserById(userId: UUID): Optional<User> {
        return userRepository.findById(userId)
    }

    // Find a user by email
    fun findUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    // Find a user by username
    fun findUserByUsername(username: String): Optional<User> {
        return userRepository.findByUsername(username)
    }

    // Check if a user exists by email
    fun isEmailTaken(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    // Check if a username is already taken
    fun isUsernameTaken(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    // Get all users by role
    fun getUsersByRole(role: String): List<User> {
        return userRepository.findAllByRole(role)
    }

    // Get all users by status
    fun getUsersByStatus(status: String): List<User> {
        return userRepository.findAllByStatus(status)
    }

    // Delete a user by ID
    fun deleteUser(userId: UUID) {
        userRepository.deleteById(userId)
    }
}

package com.netflixclone.user.controllers

import com.netflixclone.user.domain.User
import com.netflixclone.user.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    // Create a new user
    @PostMapping("/create")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.saveUser(user))
    }

    // Get a user by ID
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.findUserById(id)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Get a user by email
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userService.findUserByEmail(email)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Get a user by username
    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        val user = userService.findUserByUsername(username)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Get all users by role
    @GetMapping("/role/{role}")
    fun getUsersByRole(@PathVariable role: String): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.getUsersByRole(role))
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.ok().build()
    }
}
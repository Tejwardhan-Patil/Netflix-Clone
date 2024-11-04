package com.website.controllers

import com.website.domain.User
import com.website.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(@Autowired private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@RequestBody user: User): ResponseEntity<Any> {
        return try {
            val newUser = userService.registerUser(user)
            ResponseEntity(newUser, HttpStatus.CREATED)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody credentials: Map<String, String>): ResponseEntity<Any> {
        return try {
            val email = credentials["email"] ?: return ResponseEntity("Email is required", HttpStatus.BAD_REQUEST)
            val password = credentials["password"] ?: return ResponseEntity("Password is required", HttpStatus.BAD_REQUEST)
            val loggedInUser = userService.loginUser(email, password)
            ResponseEntity(loggedInUser, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(id)
            ResponseEntity(user, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody user: User
    ): ResponseEntity<Any> {
        return try {
            val updatedUser = userService.updateUser(id, user)
            ResponseEntity(updatedUser, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            userService.deleteUser(id)
            ResponseEntity("User deleted successfully", HttpStatus.NO_CONTENT)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/email")
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<Any> {
        return try {
            val user = userService.getUserByEmail(email)
            ResponseEntity(user, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/password")
    fun updatePassword(
        @RequestParam email: String,
        @RequestParam oldPassword: String,
        @RequestParam newPassword: String
    ): ResponseEntity<Any> {
        return try {
            userService.updatePassword(email, oldPassword, newPassword)
            ResponseEntity("Password updated successfully", HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return try {
            val users = userService.getAllUsers()
            ResponseEntity(users, HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
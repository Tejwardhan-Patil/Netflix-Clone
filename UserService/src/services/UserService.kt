package com.netflixclone.userservice.services

import com.netflixclone.userservice.domain.User
import com.netflixclone.userservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
) {

    // Create a new user in the system
    fun createUser(user: User): User {
        if (userRepository.existsByEmail(user.email)) {
            throw IllegalArgumentException("Email already exists")
        }
        return userRepository.save(user)
    }

    // Fetch user by ID
    fun getUserById(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }

    // Fetch all users
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // Update user details
    fun updateUser(userId: Long, updatedUser: User): User {
        val existingUser = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        existingUser.firstName = updatedUser.firstName
        existingUser.lastName = updatedUser.lastName
        existingUser.email = updatedUser.email
        existingUser.password = updatedUser.password
        existingUser.dateOfBirth = updatedUser.dateOfBirth

        return userRepository.save(existingUser)
    }

    // Delete user by ID
    fun deleteUser(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User not found")
        }
        userRepository.deleteById(userId)
    }

    // Check if email exists in the system
    fun emailExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    // Activate user account
    fun activateUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (user.isActive) {
            throw IllegalStateException("User already active")
        }

        user.isActive = true
        return userRepository.save(user)
    }

    // Deactivate user account
    fun deactivateUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (!user.isActive) {
            throw IllegalStateException("User already inactive")
        }

        user.isActive = false
        return userRepository.save(user)
    }

    // Reset password
    fun resetPassword(userId: Long, newPassword: String): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.password = newPassword
        return userRepository.save(user)
    }

    // Suspend user account
    fun suspendUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (user.isSuspended) {
            throw IllegalStateException("User is already suspended")
        }

        user.isSuspended = true
        return userRepository.save(user)
    }

    // Reactivate suspended user account
    fun reactivateUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (!user.isSuspended) {
            throw IllegalStateException("User is not suspended")
        }

        user.isSuspended = false
        return userRepository.save(user)
    }

    // Soft delete user (mark user as deleted)
    fun softDeleteUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (user.isDeleted) {
            throw IllegalStateException("User is already deleted")
        }

        user.isDeleted = true
        return userRepository.save(user)
    }

    // Restore soft-deleted user
    fun restoreUser(userId: Long): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        if (!user.isDeleted) {
            throw IllegalStateException("User is not deleted")
        }

        user.isDeleted = false
        return userRepository.save(user)
    }

    // Update user roles
    fun updateUserRoles(userId: Long, roles: Set<String>): User {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.roles = roles
        return userRepository.save(user)
    }

    // Fetch users by role
    fun getUsersByRole(role: String): List<User> {
        return userRepository.findByRolesContaining(role)
    }

    // Fetch active users
    fun getActiveUsers(): List<User> {
        return userRepository.findByIsActive(true)
    }

    // Fetch inactive users
    fun getInactiveUsers(): List<User> {
        return userRepository.findByIsActive(false)
    }

    // Fetch suspended users
    fun getSuspendedUsers(): List<User> {
        return userRepository.findByIsSuspended(true)
    }

    // Fetch users who were soft deleted
    fun getSoftDeletedUsers(): List<User> {
        return userRepository.findByIsDeleted(true)
    }
}
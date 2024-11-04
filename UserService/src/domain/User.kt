package com.website.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name")
    var firstName: String,

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name")
    var lastName: String,

    @NotBlank
    @Email
    @Column(name = "email", unique = true)
    var email: String,

    @NotBlank
    @Size(min = 8)
    @Column(name = "password")
    var password: String,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: Set<String> = setOf("USER"),

    @Column(name = "account_non_expired")
    var accountNonExpired: Boolean = true,

    @Column(name = "account_non_locked")
    var accountNonLocked: Boolean = true,

    @Column(name = "credentials_non_expired")
    var credentialsNonExpired: Boolean = true,

    @Column(name = "enabled")
    var enabled: Boolean = true
) : UserDetails {

    // UserDetails interface methods
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { GrantedAuthority { it } }
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    // Utility functions
    fun updatePassword(newPassword: String) {
        password = newPassword
        updatedAt = LocalDateTime.now()
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        updatedAt = LocalDateTime.now()
    }

    fun addRole(role: String) {
        roles = roles.plus(role)
        updatedAt = LocalDateTime.now()
    }

    fun removeRole(role: String) {
        roles = roles.minus(role)
        updatedAt = LocalDateTime.now()
    }

    override fun toString(): String {
        return "User(id=$id, firstName='$firstName', lastName='$lastName', email='$email', roles=$roles, enabled=$enabled)"
    }

    companion object {
        fun createNewUser(firstName: String, lastName: String, email: String, password: String): User {
            return User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        }
    }
}
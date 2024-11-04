package com.netflixclone.app.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("membershipType")
    val membershipType: MembershipType,

    @SerializedName("watchHistory")
    val watchHistory: List<Movie> = emptyList(),

    @SerializedName("watchlist")
    val watchlist: List<Movie> = emptyList(),

    @SerializedName("subscriptionStartDate")
    val subscriptionStartDate: Long,

    @SerializedName("subscriptionEndDate")
    val subscriptionEndDate: Long? = null,

    @SerializedName("preferences")
    val preferences: Preferences = Preferences(),

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("lastLogin")
    val lastLogin: Long = System.currentTimeMillis()
) : Parcelable {
    // Helper function to check if subscription is valid
    fun isSubscriptionActive(): Boolean {
        return subscriptionEndDate?.let { it > System.currentTimeMillis() } ?: true
    }

    // Helper function to toggle the watchlist
    fun toggleWatchlist(movie: Movie): User {
        return if (watchlist.contains(movie)) {
            copy(watchlist = watchlist.filter { it.id != movie.id })
        } else {
            copy(watchlist = watchlist + movie)
        }
    }

    // Helper function to add a movie to watch history
    fun addToWatchHistory(movie: Movie): User {
        return copy(watchHistory = watchHistory + movie)
    }
}

enum class MembershipType {
    BASIC,
    STANDARD,
    PREMIUM
}

@Parcelize
data class Preferences(
    @SerializedName("language")
    val language: String = "English",

    @SerializedName("playbackQuality")
    val playbackQuality: PlaybackQuality = PlaybackQuality.HD,

    @SerializedName("subtitles")
    val subtitles: Boolean = true,

    @SerializedName("autoplay")
    val autoplay: Boolean = true
) : Parcelable

enum class PlaybackQuality {
    SD,
    HD,
    ULTRA_HD
}

// Movie model class
@Parcelize
data class Movie(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("posterUrl")
    val posterUrl: String? = null,

    @SerializedName("releaseDate")
    val releaseDate: Long,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("genres")
    val genres: List<String>
) : Parcelable
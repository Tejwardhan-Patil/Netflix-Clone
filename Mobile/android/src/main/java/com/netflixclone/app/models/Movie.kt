package com.netflixclone.app.models

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseDate: String,
    val genres: List<Genre>,
    val cast: List<Cast>,
    val director: String,
    val duration: Int, // Duration in minutes
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false
) : Parcelable {

    // Constructor for parcelable
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        title = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        posterUrl = parcel.readString() ?: "",
        backdropUrl = parcel.readString() ?: "",
        rating = parcel.readDouble(),
        releaseDate = parcel.readString() ?: "",
        genres = parcel.createTypedArrayList(Genre) ?: listOf(),
        cast = parcel.createTypedArrayList(Cast) ?: listOf(),
        director = parcel.readString() ?: "",
        duration = parcel.readInt(),
        isFavorite = parcel.readByte() != 0.toByte(),
        isDownloaded = parcel.readByte() != 0.toByte()
    )

    // Parcelable write method
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(posterUrl)
        parcel.writeString(backdropUrl)
        parcel.writeDouble(rating)
        parcel.writeString(releaseDate)
        parcel.writeTypedList(genres)
        parcel.writeTypedList(cast)
        parcel.writeString(director)
        parcel.writeInt(duration)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isDownloaded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }

    // Utility method to get genre names as a string
    fun getGenresString(): String {
        return genres.joinToString(", ") { it.name }
    }

    // Utility method to get cast names as a string
    fun getCastString(): String {
        return cast.joinToString(", ") { it.name }
    }

    // Calculate formatted duration like "2h 15m"
    fun getFormattedDuration(): String {
        val hours = duration / 60
        val minutes = duration % 60
        return if (hours > 0) {
            "$hours h $minutes m"
        } else {
            "$minutes m"
        }
    }

    // Formatted release year for displaying in the UI
    fun getReleaseYear(): String {
        return releaseDate.split("-")[0]
    }

    // Update favorite status
    fun toggleFavorite(): Movie {
        return this.copy(isFavorite = !this.isFavorite)
    }

    // Update downloaded status
    fun toggleDownloaded(): Movie {
        return this.copy(isDownloaded = !this.isDownloaded)
    }
}

data class Genre(
    val id: Int,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Genre> {
        override fun createFromParcel(parcel: Parcel): Genre {
            return Genre(parcel)
        }

        override fun newArray(size: Int): Array<Genre?> {
            return arrayOfNulls(size)
        }
    }
}

data class Cast(
    val id: Int,
    val name: String,
    val character: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(character)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cast> {
        override fun createFromParcel(parcel: Parcel): Cast {
            return Cast(parcel)
        }

        override fun newArray(size: Int): Array<Cast?> {
            return arrayOfNulls(size)
        }
    }
}
package com.dignerdranch.anroid.pract_5_2_hojaeva

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val name: String,
    val year: Int?,
    val description: String?,
    val movieLength: Int?,

    var genre: String? = "",
    var ratingString: String? = "",
    var countryString: String? = "",
    var userNote: String? = "",

    var localRating: String? = "",
    var localCountries: String? = ""
) {
    @Ignore var rating: NetworkRating? = null
    @Ignore var countries: List<NetworkCountry>? = null
    @Ignore var genres: List<NetworkGenre>? = null

    fun prepareForRoom() {
        val roundedRating = rating?.kp?.let { String.format("%.1f", it) }
        localRating = roundedRating ?: "-"

        val joined = countries?.joinToString(", ") { it.name ?: "" }
        localCountries = if (joined.isNullOrEmpty()) "-" else joined

        if (!genres.isNullOrEmpty()) {
            genre = genres?.joinToString(", ") { it.name ?: "" }
        }
    }
}
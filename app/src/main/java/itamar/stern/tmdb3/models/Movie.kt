package itamar.stern.tmdb3.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Movies")
data class Movie (
    val adult: Boolean?,
    val backdrop_path: String?,
    @PrimaryKey
    val id: Long?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Long?,
)


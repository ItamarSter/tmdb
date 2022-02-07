package itamar.stern.tmdb3.models

import androidx.room.Entity
import itamar.stern.tmdb3.models.Movie

@Entity
data class MovieResponse(
    val page: Long,
    val results: List<Movie>,
    val total_pages: Long,
    val total_results: Long
)
package itamar.stern.tmdb3.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteMovies")
data class FavoriteMovie (
    @PrimaryKey
    val id: Long?
)
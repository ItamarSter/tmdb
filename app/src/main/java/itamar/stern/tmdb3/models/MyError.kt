package itamar.stern.tmdb3.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Errors")
data class MyError(
    val dateTime: String,
    val id: String,
    @PrimaryKey
    val message: String
)
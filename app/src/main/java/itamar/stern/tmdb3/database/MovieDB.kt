package itamar.stern.tmdb3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import itamar.stern.tmdb3.database.dao.MovieDao
import itamar.stern.tmdb3.models.FavoriteMovie
import itamar.stern.tmdb3.models.MyError
import itamar.stern.tmdb3.models.Movie

const val DB_VERSION = 1
const val DB_NAME = "MoviesDatabase"

@Database(entities = [Movie::class, MyError::class, FavoriteMovie::class], version = DB_VERSION)
abstract class MovieDB : RoomDatabase() {
    companion object {
        fun create(context: Context): MovieDB {
            return Room.databaseBuilder(context, MovieDB::class.java, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    abstract fun movieDao(): MovieDao
}
package itamar.stern.tmdb3.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import itamar.stern.tmdb3.models.FavoriteMovie
import itamar.stern.tmdb3.models.MyError
import itamar.stern.tmdb3.models.Movie

@Dao
interface MovieDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovies(movies: List<Movie>)

    @Query("SELECT * FROM Movies WHERE NOT title LIKE '%sex%' AND NOT title LIKE '%porn%'")
    fun getMovies(): LiveData<List<Movie>>

    @Query("SELECT COUNT(id) FROM Movies")
    fun getAmount(): LiveData<Int>

    @Query("SELECT * FROM Movies WHERE NOT title LIKE '%sex%' AND NOT title LIKE '%porn%' ORDER BY title")
    fun getMoviesByTitle(): LiveData<List<Movie>>

    @Query("SELECT * FROM Movies WHERE NOT title LIKE '%sex%' AND NOT title LIKE '%porn%' ORDER BY release_date DESC")
    fun getMoviesByReleaseDate(): LiveData<List<Movie>>

    @Query("SELECT * FROM Movies WHERE NOT title LIKE '%sex%' AND NOT title LIKE '%porn%' ORDER BY popularity DESC")
    fun getMoviesByPopularity(): LiveData<List<Movie>>

    @Query("SELECT * FROM Movies WHERE title LIKE :search AND NOT title LIKE '%sex%' AND NOT title LIKE '%porn%'")
    fun getSearchResult(search: String): List<Movie>

    //Favorites table:

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(favorite: FavoriteMovie)

    @Query("DELETE FROM FavoriteMovies WHERE id=:id")
    fun deleteFavorite(id: Long)

    @Query("DELETE FROM FavoriteMovies")
    fun clearFavorites()

    @Query("SELECT * FROM FavoriteMovies WHERE id=:id")
    fun checkFavoriteExists(id: Long): List<FavoriteMovie>

    @Query("SELECT * FROM FavoriteMovies")
    fun getFavoritesIds(): List<FavoriteMovie>

    @Query("SELECT * FROM Movies WHERE id IN (:ids)")
    fun getFavorites(ids: List<Long>): LiveData<List<Movie>>

    //Errors table:

    @Query("SELECT * FROM Errors")
    fun getErrors(): List<MyError>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addError(error: MyError)
}
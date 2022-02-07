package itamar.stern.tmdb3.repository


import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.database.dao.MovieDao
import itamar.stern.tmdb3.network.MoviesApi
import itamar.stern.tmdb3.ui.movies.MoviesFragment
import itamar.stern.tmdb3.utils.catchError


class MovieRepository(
    private val movieDao: MovieDao,
    private val moviesApi: MoviesApi
) {
    val movies = movieDao.getMovies()
    val moviesByTitle = movieDao.getMoviesByTitle()
    val moviesByDate = movieDao.getMoviesByReleaseDate()
    val moviesByVote = movieDao.getMoviesByPopularity()

    suspend fun fetchAndEnterMovies(from: Int, to:Int) {
        for (i in from..to) {
            try {
                val movies = moviesApi.fetchMovies(i).results
                movieDao.addMovies(movies)
            } catch (e: Exception) {
                catchError(e)
                if(MoviesFragment.amount.value!! < 9000){
                    MovieApplication.scheduleDownloads()
                } else {
                    MovieApplication.scheduleRefresh()
                }
                return
            }
        }
    }

}
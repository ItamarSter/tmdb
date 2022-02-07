package itamar.stern.tmdb3.network

import itamar.stern.tmdb3.BuildConfig
import itamar.stern.tmdb3.models.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.themoviedb.org/"
const val API_KEY = BuildConfig.TMDB_API_KEY
interface MoviesApi {
    @GET("3/discover/movie")
    suspend fun fetchMovies(
        @Query("page") page: Int = 1,
        @Query("api_key") api_key: String = API_KEY
    ): MovieResponse

    companion object {
        fun create(): MoviesApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoviesApi::class.java)
        }
    }
}
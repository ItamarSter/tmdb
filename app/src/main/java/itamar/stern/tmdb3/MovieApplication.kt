package itamar.stern.tmdb3

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import itamar.stern.tmdb3.database.MovieDB
import itamar.stern.tmdb3.models.Movie
import itamar.stern.tmdb3.network.MoviesApi
import itamar.stern.tmdb3.repository.MovieRepository
import itamar.stern.tmdb3.ui.movies.MoviesFragment
import itamar.stern.tmdb3.workers.*

class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MovieApplication

        lateinit var workManager : WorkManager
        fun isWorkInited() = this::workManager.isInitialized

        fun scheduleDownloads() {
            for (i in 1..500 step 25){
                scheduleDownload(i, i+24)
            }
        }
        private fun scheduleDownload(from: Int, to: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(Data.Builder().putInt("from", from).putInt("to", to).build())
                .build()
            if(!isWorkInited()){
                workManager = WorkManager.getInstance(instance)
            }
            workManager.enqueue(request)
        }
        fun scheduleRefresh() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(Data.Builder().putInt("from", 1).putInt("to", 500).build())
                .setConstraints(constraints)
                .build()
            if(!isWorkInited()){
                workManager = WorkManager.getInstance(instance)
            }
            workManager.enqueue(request)
        }


        val db: MovieDB by lazy {
            MovieDB.create(instance)
        }

        val repository: MovieRepository by lazy {
            MovieRepository(db.movieDao(), MoviesApi.create())
        }

        lateinit var focusedMovie: Movie

        val prefs: SharedPreferences by lazy {
            instance.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }

        val fireDBRef: DatabaseReference by lazy {
            FirebaseDatabase.getInstance().reference
        }
        //name and color for the current user messages:
        var name = MutableLiveData<String>()
        var color = MutableLiveData<String>()

        fun refreshAndShowFavorites(){
            val ids = db.movieDao().getFavoritesIds()
            val idsList = mutableListOf<Long>()
            for (id in ids) {
                idsList.add(id.id!!)
            }
            MoviesFragment.movies = db.movieDao().getFavorites(idsList)
        }
    }
}
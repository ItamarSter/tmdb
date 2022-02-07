package itamar.stern.tmdb3.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import itamar.stern.tmdb3.MovieApplication

class DownloadWorker(context: Context, workerParameters: WorkerParameters):CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        try{
            MovieApplication.repository.fetchAndEnterMovies(inputData.getInt("from", 1), inputData.getInt("to", 500))
        } catch (e: Exception){
            return Result.failure()
        }
        return Result.success()
    }

}
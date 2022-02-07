package itamar.stern.tmdb3.utils

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.util.Patterns
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.models.MyError
import java.time.LocalDateTime
import java.util.*

fun CharSequence.isEmailValid(): Boolean{
    return this.length >= 6 && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun hideKeyboard(activity: Activity) {
    //system service that can hide the keyboard:
    val imm = activity.getSystemService(InputMethodManager::class.java)
    //which edittext
    imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}

fun Number.dp(): Float{
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)
}

fun catchError(e: Exception){
    MovieApplication.db.movieDao()
        .addError(
            MyError(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    LocalDateTime.now().toString()
                else
                    Calendar.getInstance().time.toString(),
                UUID.randomUUID().toString(),
                e.localizedMessage.toString()
            )
        )
}

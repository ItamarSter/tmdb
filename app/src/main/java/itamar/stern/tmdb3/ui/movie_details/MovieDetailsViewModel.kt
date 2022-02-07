package itamar.stern.tmdb3.ui.movie_details


import android.app.Application
import android.os.Build
import android.text.Editable
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.models.Comment
import java.time.LocalDate
import java.util.*

class MovieDetailsViewModel(application: Application) : AndroidViewModel(application) {
    //Done first download -> we can hide progressBar:
    var done = MutableLiveData(false)

    val comments = MutableLiveData<MutableList<Comment>>(mutableListOf())

    init {
        readFromDBIncremental()
    }

    private fun readFromDBIncremental() {
        val ref = MovieApplication.fireDBRef.child("comments").child(MovieApplication.focusedMovie.id.toString())
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                done.value = true
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val comment = snapshot.getValue<Comment>()
                if (comment != null) {
                    comments.value?.add(comment)
                    comments.value = comments.value
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })
    }


    fun sendComment(messageField: Editable) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val dateString = makeStringDate()
        MovieApplication.fireDBRef.child("comments")
            .child(MovieApplication.focusedMovie.id.toString())
            .push()
            .setValue(
                Comment(
                    uid,
                    messageField.toString(),
                    MovieApplication.prefs.getString("currentName", null),
                    MovieApplication.prefs.getString("currentColor", null),
                    dateString
                )
            )
            .addOnSuccessListener {
                messageField.clear()
            }
            .addOnFailureListener {
                Toast.makeText(
                    getApplication(),
                    "Something went wrong, try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun makeStringDate(): String {
        val day: Int
        val month: Int
        val year: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            day = LocalDate.now().dayOfMonth
            month = LocalDate.now().monthValue
            year = LocalDate.now().year
        } else {
            day = Calendar.getInstance().time.date
            month = Calendar.getInstance().time.month + 1
            year = Calendar.getInstance().time.year + 1900
        }
        return "${day}/${month}/${year}"
    }
}
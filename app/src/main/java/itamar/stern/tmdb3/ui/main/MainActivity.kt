package itamar.stern.tmdb3.ui.main


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.ui.auth.AuthActivity
import itamar.stern.tmdb3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbarMain)
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        //start fetch/refresh movies before register/login, to save download time.
        if (MovieApplication.prefs.getString("firstTimeApp", null) == null) {
            MovieApplication.scheduleDownloads()
            MovieApplication.prefs.edit().putString("firstTimeApp", "yes").apply()
        } else if (!MovieApplication.isWorkInited()) {
            MovieApplication.scheduleRefresh()
        }

        //Set listener to logout - and send the user to auth:
        FirebaseAuth.getInstance().addAuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
            }
        }
    }
}
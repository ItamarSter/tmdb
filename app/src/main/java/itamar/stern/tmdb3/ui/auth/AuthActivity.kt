package itamar.stern.tmdb3.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.tmdb3.ui.main.MainActivity
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.databinding.ActivityAuthBinding
import itamar.stern.tmdb3.models.AppUser
import itamar.stern.tmdb3.utils.hideKeyboard
import itamar.stern.tmdb3.utils.isEmailValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val email get() = binding.content.editTextEmail.text.toString()
    private val password get() = binding.content.editTextPassword.text.toString()
    private val name get() = binding.content.editTextName.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()

        with(binding.content) {
            buttonLogin.setOnClickListener {
                login()
            }
            buttonRegister.setOnClickListener {
                register()
            }
            textViewFirstTime.setOnClickListener {
                //if "first time" is pressed - replace the login button with register button,
                //open the name editText (just for register),
                //replace the "first time" text to "already have...?".
                buttonRegister.visibility = View.VISIBLE
                buttonLogin.visibility = View.INVISIBLE
                editTextName.visibility = View.VISIBLE
                textViewFirstTime.visibility = View.INVISIBLE
                textViewGoToLogin.visibility = View.VISIBLE
            }
            textViewGoToLogin.setOnClickListener {
                //if "already have...?" is pressed - replace the register button with login button,
                //hide the name editText,
                //replace the "already have...?" text to "first time".
                buttonRegister.visibility = View.INVISIBLE
                buttonLogin.visibility = View.VISIBLE
                editTextName.visibility = View.GONE
                textViewGoToLogin.visibility = View.INVISIBLE
                textViewFirstTime.visibility = View.VISIBLE
            }
        }

    }

    private fun register() {
        hideKeyboard(this)
        if (!isValidDetailsRegister()) {
            return
        }
        binding.content.textViewGoToLogin.isEnabled = false
        binding.content.buttonRegister.visibility = View.INVISIBLE
        showProgressBar()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccessRegister)
            .addOnFailureListener(onFailureRegister)
    }

    private fun login() {
        hideKeyboard(this)
        if (!isValidDetailsLogin()) {
            return
        }
        binding.content.textViewFirstTime.isEnabled = false
        binding.content.buttonLogin.visibility = View.INVISIBLE
        showProgressBar()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccessLogin)
            .addOnFailureListener(onFailureLogin)
    }

    private val onSuccessRegister = OnSuccessListener<AuthResult> {
        hideProgressBar()
        val color = Color.rgb(
            (Math.random() * 256).toInt(),
            (Math.random() * 256).toInt(),
            (Math.random() * 256).toInt()
        )
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        MovieApplication.fireDBRef.child("users").child(uid).setValue(
            AppUser(
                email,
                uid,
                name,
                color
            )
        )
        saveNameAndColorInPrefs(name, color, uid)
        saveCurrentNameColor()
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }

    private val onSuccessLogin = OnSuccessListener<AuthResult> {
        hideProgressBar()
        if(MovieApplication.prefs.getString("${FirebaseAuth.getInstance().currentUser!!.uid}#name", null) == null){
            //if it's null it's mean it's not the device which the user registered in it.
            // So we need to take the name and color from firebase:
            saveCurrentNameColorFromFirebase()
        } else {
            saveCurrentNameColor()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val onFailureLogin = OnFailureListener {
        hideProgressBar()
        binding.content.textViewFirstTime.isEnabled = true
        binding.content.buttonLogin.visibility = View.VISIBLE
        showSnackbar(it.localizedMessage)
    }
    private val onFailureRegister = OnFailureListener {
        hideProgressBar()
        binding.content.textViewGoToLogin.isEnabled = true
        binding.content.buttonRegister.visibility = View.VISIBLE
        showSnackbar(it.localizedMessage)
    }

    private fun saveNameAndColorInPrefs(name: String, color: Int, uid: String) {
        MovieApplication.prefs.edit().putString("$uid#name", name).apply()
        MovieApplication.prefs.edit().putLong("$uid#color", color.toLong()).apply()
    }

    private fun saveCurrentNameColor(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        MovieApplication.name.value = MovieApplication.prefs.getString("$uid#name", null)
        MovieApplication.color.value = MovieApplication.prefs.getLong("$uid#color", 0).toString()
    }

    //SaveCurrentNameColorFromFirebase is for scenario which the user login from
    //device which he didn't registered in it, and the his name and color wasn't saved
    //in SharedPreferences. So we need to get it from firebase and put it in prefs and in current name and color.
    private fun saveCurrentNameColorFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        //Using coroutines - to ensure we get the name and color even if the
        //activity already get closed.
        lifecycleScope.launch (Dispatchers.IO){
            MovieApplication.fireDBRef.child("users").child(uid).get()
                .addOnSuccessListener {
                    MovieApplication.prefs.edit().putString("$uid#name", it.child("name").value.toString()).apply()
                    MovieApplication.prefs.edit().putLong("$uid#color", it.child("color").value as Long).apply()
                    MovieApplication.name.value = it.child("name").value.toString()
                    MovieApplication.color.value = it.child("color").value.toString()
                }
        }

    }
    private fun isValidDetailsLogin(): Boolean {
        if (!checkEmail() || !checkPassword()) {
            showSnackbar("Email or password not valid")
            return false
        }
        return true
    }

    private fun isValidDetailsRegister(): Boolean {
        if (!checkEmail() || !checkPassword() || !checkName()) {
            showSnackbar("Email or password or name not valid")
            return false
        }
        return true
    }

    private fun checkEmail(): Boolean = email.isEmailValid()
    private fun checkPassword(): Boolean = password.length >= 6
    private fun checkName(): Boolean = name.length >= 2

    private fun showProgressBar() {
        binding.content.progressBarAuth.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.content.progressBarAuth.visibility = View.INVISIBLE
    }

    private fun showSnackbar(message: CharSequence) {
        Snackbar.make(
            binding.content.buttonLogin,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
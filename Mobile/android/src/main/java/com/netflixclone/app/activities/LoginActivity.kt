package com.netflixclone.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.netflixclone.app.R
import com.netflixclone.app.services.AuthService
import com.netflixclone.app.utils.NetworkUtils
import com.netflixclone.app.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var authService: AuthService
    private lateinit var sessionManager: SessionManager
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.progressBar)

        // Initialize services
        authService = AuthService()
        sessionManager = SessionManager(this)

        // Set login button click listener
        loginButton.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                login()
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Login function
    private fun login() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        authService.login(email, password, object : AuthService.AuthCallback {
            override fun onSuccess(token: String) {
                progressBar.visibility = View.GONE
                sessionManager.saveAuthToken(token)
                navigateToMainScreen()
            }

            override fun onFailure(message: String) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Login Failed: $message", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Navigate to main screen
    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainScreen()
        }
    }

    override fun onBackPressed() {
        // Disable back press on login screen
    }
}

// AuthService.kt
package com.netflixclone.app.services

import android.os.Handler
import android.os.Looper

class AuthService {

    interface AuthCallback {
        fun onSuccess(token: String)
        fun onFailure(message: String)
    }

    // Simulated login function
    fun login(email: String, password: String, callback: AuthCallback) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (email == "user@website.com" && password == "password123") {
                callback.onSuccess("token123")
            } else {
                callback.onFailure("Invalid email or password")
            }
        }, 2000)
    }
}

// SessionManager.kt
package com.netflixclone.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("netflix_clone", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}

// NetworkUtils.kt
package com.netflixclone.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
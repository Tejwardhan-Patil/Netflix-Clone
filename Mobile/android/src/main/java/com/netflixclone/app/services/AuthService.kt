package com.netflixclone.app.services

import android.content.Context
import com.netflixclone.app.models.User
import com.netflixclone.app.utils.NetworkUtils
import com.netflixclone.app.utils.TokenManager
import com.netflixclone.app.api.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AuthService(private val context: Context) {

    private val apiService = ApiService.create()

    // Method for signing up a new user
    fun signUp(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val user = User(email = email, password = password, username = username)
        val call = apiService.signUp(user)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback(true, "User registered successfully")
                } else {
                    callback(false, "Failed to register user: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Signup request failed: ${t.message}")
            }
        })
    }

    // Method for logging in an existing user
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val user = User(email = email, password = password)
        val call = apiService.login(user)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val token = response.headers()["Authorization"]
                    token?.let {
                        TokenManager.saveToken(context, it)
                        callback(true, "Login successful")
                    } ?: callback(false, "No token received")
                } else {
                    callback(false, "Login failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Login request failed: ${t.message}")
            }
        })
    }

    // Method for logging out the current user
    fun logout(callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val token = TokenManager.getToken(context)
        if (token == null) {
            callback(false, "User is not logged in")
            return
        }

        val call = apiService.logout("Bearer $token")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    TokenManager.clearToken(context)
                    callback(true, "Logout successful")
                } else {
                    callback(false, "Logout failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Logout request failed: ${t.message}")
            }
        })
    }

    // Method to fetch current authenticated user details
    fun getCurrentUser(callback: (User?, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(null, "Network unavailable")
            return
        }

        val token = TokenManager.getToken(context)
        if (token == null) {
            callback(null, "User is not logged in")
            return
        }

        val call = apiService.getCurrentUser("Bearer $token")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    callback(user, null)
                } else {
                    callback(null, "Failed to fetch user: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "Request failed: ${t.message}")
            }
        })
    }

    // Method for password reset request
    fun requestPasswordReset(email: String, callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val call = apiService.resetPassword(email)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback(true, "Password reset request sent")
                } else {
                    callback(false, "Failed to send password reset: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Request failed: ${t.message}")
            }
        })
    }

    // Method for changing password
    fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val token = TokenManager.getToken(context)
        if (token == null) {
            callback(false, "User is not logged in")
            return
        }

        val call = apiService.changePassword("Bearer $token", oldPassword, newPassword)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback(true, "Password changed successfully")
                } else {
                    callback(false, "Failed to change password: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Change password request failed: ${t.message}")
            }
        })
    }

    // Method for token refresh
    fun refreshToken(callback: (Boolean, String?) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback(false, "Network unavailable")
            return
        }

        val token = TokenManager.getToken(context)
        if (token == null) {
            callback(false, "No token to refresh")
            return
        }

        val call = apiService.refreshToken("Bearer $token")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val newToken = response.headers()["Authorization"]
                    newToken?.let {
                        TokenManager.saveToken(context, it)
                        callback(true, "Token refreshed")
                    } ?: callback(false, "No token received")
                } else {
                    callback(false, "Failed to refresh token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(false, "Token refresh request failed: ${t.message}")
            }
        })
    }
}
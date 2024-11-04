package com.netflixclone.app.services

import android.content.Context
import android.content.SharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Call
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

// API Service Interface
interface ApiService {

    @GET("movies")
    fun getMovies(): Call<List<Movie>>

    @GET("movies/{id}")
    fun getMovieDetails(@Path("id") id: Int): Call<Movie>

    @POST("auth/login")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @POST("auth/register")
    @FormUrlEncoded
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @POST("favorites")
    fun addFavorite(
        @Header("Authorization") token: String,
        @Body movie: Movie
    ): Call<FavoriteResponse>

    @DELETE("favorites/{id}")
    fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("id") movieId: Int
    ): Call<Void>
}

// Data Classes for API Responses
data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val rating: Double
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)

data class FavoriteResponse(
    val success: Boolean,
    val message: String
)

// Retrofit Client Setup
object ApiClient {

    private const val BASE_URL = "https://api.website.com/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun create(): ApiService = retrofit.create(ApiService::class.java)
}

// Interceptor for adding Authorization header
class AuthInterceptor(context: Context) : Interceptor {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Retrieving token from shared preferences
        val token = sharedPreferences.getString("auth_token", null)

        val requestBuilder = originalRequest.newBuilder()
        
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}

// Utility for handling API responses
object ApiUtil {
    fun <T> handleApiResponse(
        call: Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (String) -> Unit
    ) {
        call.enqueue(object : retrofit2.Callback<T> {
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: run {
                        onFailure("Empty Response")
                    }
                } else {
                    onFailure("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(t.message ?: "Unknown Error")
            }
        })
    }
}

// Usage
class MovieRepository(context: Context) {

    private val apiService: ApiService = ApiClient.create()

    // Pass context for AuthInterceptor
    private val authInterceptor = AuthInterceptor(context)

    fun getMovies(
        onSuccess: (List<Movie>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.getMovies()
        ApiUtil.handleApiResponse(call, onSuccess, onFailure)
    }

    fun getMovieDetails(
        id: Int,
        onSuccess: (Movie) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.getMovieDetails(id)
        ApiUtil.handleApiResponse(call, onSuccess, onFailure)
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.loginUser(email, password)
        ApiUtil.handleApiResponse(call, onSuccess, onFailure)
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.registerUser(name, email, password)
        ApiUtil.handleApiResponse(call, onSuccess, onFailure)
    }

    fun addFavorite(
        token: String,
        movie: Movie,
        onSuccess: (FavoriteResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.addFavorite(token, movie)
        ApiUtil.handleApiResponse(call, onSuccess, onFailure)
    }

    fun removeFavorite(
        token: String,
        movieId: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = apiService.removeFavorite(token, movieId)
        ApiUtil.handleApiResponse(call, { onSuccess() }, onFailure)
    }
}
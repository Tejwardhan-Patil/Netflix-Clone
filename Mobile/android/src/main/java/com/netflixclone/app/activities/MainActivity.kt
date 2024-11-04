package com.netflixclone.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.adapters.CategoryAdapter
import com.netflixclone.app.adapters.MovieAdapter
import com.netflixclone.app.models.Movie
import com.netflixclone.app.services.ApiService
import com.netflixclone.app.services.AuthService
import com.netflixclone.app.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewMovies: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var movieAdapter: MovieAdapter
    private val apiService = ApiService()
    private val authService = AuthService()
    private val moviesList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupAdapters()
        fetchMovies()
    }

    private fun initViews() {
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories)
        recyclerViewMovies = findViewById(R.id.recyclerViewMovies)

        recyclerViewCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewMovies.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter()
        movieAdapter = MovieAdapter(moviesList, object : MovieAdapter.OnItemClickListener {
            override fun onItemClick(movie: Movie) {
                openMovieDetail(movie)
            }
        })

        recyclerViewCategories.adapter = categoryAdapter
        recyclerViewMovies.adapter = movieAdapter
    }

    private fun fetchMovies() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val moviesResponse = apiService.getMovies()
                    if (moviesResponse.isSuccessful) {
                        moviesResponse.body()?.let { movies ->
                            withContext(Dispatchers.Main) {
                                moviesList.clear()
                                moviesList.addAll(movies)
                                movieAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        showError("Failed to load movies")
                    }
                } catch (e: Exception) {
                    showError("Error: ${e.message}")
                }
            }
        } else {
            showError("No internet connection")
        }
    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun openMovieDetail(movie: Movie) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra("MOVIE_ID", movie.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                openProfile()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        authService.logout()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        fetchMovies()
    }

    fun onCategoryClick(view: View) {
        val categoryId = view.tag as Int
        fetchMoviesByCategory(categoryId)
    }

    private fun fetchMoviesByCategory(categoryId: Int) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val moviesResponse = apiService.getMoviesByCategory(categoryId)
                    if (moviesResponse.isSuccessful) {
                        moviesResponse.body()?.let { movies ->
                            withContext(Dispatchers.Main) {
                                moviesList.clear()
                                moviesList.addAll(movies)
                                movieAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        showError("Failed to load movies for category")
                    }
                } catch (e: Exception) {
                    showError("Error: ${e.message}")
                }
            }
        } else {
            showError("No internet connection")
        }
    }
}
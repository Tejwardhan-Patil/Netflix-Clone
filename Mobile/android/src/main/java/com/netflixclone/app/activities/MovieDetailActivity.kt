package com.netflixclone.app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.adapters.MovieAdapter
import com.netflixclone.app.models.Movie
import com.netflixclone.app.services.ApiService
import com.netflixclone.app.utils.NetworkUtils
import com.squareup.picasso.Picasso

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieDescription: TextView
    private lateinit var movieRating: TextView
    private lateinit var movieYear: TextView
    private lateinit var similarMoviesRecyclerView: RecyclerView
    private lateinit var watchTrailerButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var movie: Movie
    private lateinit var similarMoviesAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        moviePoster = findViewById(R.id.moviePoster)
        movieTitle = findViewById(R.id.movieTitle)
        movieDescription = findViewById(R.id.movieDescription)
        movieRating = findViewById(R.id.movieRating)
        movieYear = findViewById(R.id.movieYear)
        similarMoviesRecyclerView = findViewById(R.id.similarMoviesRecyclerView)
        watchTrailerButton = findViewById(R.id.watchTrailerButton)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        movie = intent.getSerializableExtra("movie") as Movie

        setMovieDetails(movie)

        setupSimilarMoviesRecyclerView()
        fetchSimilarMovies(movie.id)
        
        watchTrailerButton.setOnClickListener {
            openTrailer(movie.trailerUrl)
        }
    }

    private fun setMovieDetails(movie: Movie) {
        Picasso.get().load(movie.posterUrl).into(moviePoster)
        movieTitle.text = movie.title
        movieDescription.text = movie.description
        movieRating.text = getString(R.string.rating_format, movie.rating)
        movieYear.text = getString(R.string.year_format, movie.releaseYear)
    }

    private fun setupSimilarMoviesRecyclerView() {
        similarMoviesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        similarMoviesAdapter = MovieAdapter(emptyList()) { selectedMovie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movie", selectedMovie)
            startActivity(intent)
        }
        similarMoviesRecyclerView.adapter = similarMoviesAdapter
    }

    private fun fetchSimilarMovies(movieId: String) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            showLoading(true)
            ApiService.getSimilarMovies(movieId, object : ApiService.Callback<List<Movie>> {
                override fun onSuccess(similarMovies: List<Movie>) {
                    similarMoviesAdapter.updateMovies(similarMovies)
                    showLoading(false)
                }

                override fun onFailure(errorMessage: String) {
                    showLoading(false)
                    showError(errorMessage)
                }
            })
        } else {
            showError(getString(R.string.no_internet_connection))
        }
    }

    private fun openTrailer(trailerUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        similarMoviesRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        // Show an error message to the user, a Snackbar or a Toast.
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
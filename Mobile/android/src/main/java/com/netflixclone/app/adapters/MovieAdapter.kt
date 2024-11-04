package com.netflixclone.app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netflixclone.app.R
import com.netflixclone.app.models.Movie
import com.netflixclone.app.activities.MovieDetailActivity

class MovieAdapter(
    private val context: Context,
    private val movieList: List<Movie>
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // ViewHolder class to hold and reuse view references
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieImage: ImageView = itemView.findViewById(R.id.movieImage)
        val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        val movieCategory: TextView = itemView.findViewById(R.id.movieCategory)

        // Initialize click listener
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = movieList[position]
                    val intent = Intent(context, MovieDetailActivity::class.java).apply {
                        putExtra("movieId", movie.id)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    // Inflates the row layout from XML when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    // Binds data to each view holder when it's created
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.movieTitle.text = movie.title
        holder.movieCategory.text = movie.category

        // Load the movie image with Glide
        Glide.with(context)
            .load(movie.thumbnailUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.movieImage)
    }

    // Returns the total number of items in the data list
    override fun getItemCount(): Int {
        return movieList.size
    }
}

// Functionalities like filtering, sorting, and favorite management
fun filterMovies(category: String): List<Movie> {
    return movieList.filter { it.category.equals(category, ignoreCase = true) }
}

fun sortMoviesByTitle(ascending: Boolean = true): List<Movie> {
    return if (ascending) {
        movieList.sortedBy { it.title }
    } else {
        movieList.sortedByDescending { it.title }
    }
}

fun markMovieAsFavorite(movieId: Int): Boolean {
    val movie = movieList.find { it.id == movieId }
    return if (movie != null) {
        movie.isFavorite = !movie.isFavorite
        notifyItemChanged(movieList.indexOf(movie))
        true
    } else {
        false
    }
}

override fun getItemViewType(position: Int): Int {
    return when (position % 2 == 0) {
        true -> R.layout.item_movie_alternate
        false -> R.layout.item_movie
    }
}

// Implementing view recycling efficiently with a Payloads feature to avoid full binding
override fun onBindViewHolder(holder: MovieViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isEmpty()) {
        super.onBindViewHolder(holder, position, payloads)
    } else {
        val movie = movieList[position]
        payloads.forEach {
            when (it) {
                "title" -> holder.movieTitle.text = movie.title
                "category" -> holder.movieCategory.text = movie.category
            }
        }
    }
}

// Implementing swipe-to-delete feature
fun deleteMovieAt(position: Int) {
    (movieList as MutableList).removeAt(position)
    notifyItemRemoved(position)
}

// Interface to allow external class to interact with RecyclerView items
interface MovieItemClickListener {
    fun onMovieClick(movieId: Int)
    fun onFavoriteClick(movieId: Int)
}

var movieItemClickListener: MovieItemClickListener? = null

override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
    val movie = movieList[position]

    // Set data
    holder.movieTitle.text = movie.title
    holder.movieCategory.text = movie.category
    Glide.with(context).load(movie.thumbnailUrl).into(holder.movieImage)

    // Handle clicks
    holder.itemView.setOnClickListener {
        movieItemClickListener?.onMovieClick(movie.id)
    }

    holder.movieImage.setOnLongClickListener {
        movieItemClickListener?.onFavoriteClick(movie.id)
        true
    }
}

// Pagination support for loading movies in batches
fun addMovies(newMovies: List<Movie>) {
    val oldSize = movieList.size
    (movieList as MutableList).addAll(newMovies)
    notifyItemRangeInserted(oldSize, newMovies.size)
}

// Implementing Search functionality
fun searchMovies(query: String): List<Movie> {
    return movieList.filter {
        it.title.contains(query, ignoreCase = true) ||
        it.category.contains(query, ignoreCase = true)
    }
}
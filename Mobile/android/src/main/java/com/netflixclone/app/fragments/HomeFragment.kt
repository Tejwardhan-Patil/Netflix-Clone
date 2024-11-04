package com.netflixclone.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.activities.MovieDetailActivity
import com.netflixclone.app.adapters.CategoryAdapter
import com.netflixclone.app.adapters.MovieAdapter
import com.netflixclone.app.models.Movie
import com.netflixclone.app.models.Category
import com.netflixclone.app.services.ApiService
import com.netflixclone.app.utils.NetworkUtils
import com.netflixclone.app.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryList: List<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setupCategoryRecyclerView()
        observeCategories()
        observeMovies()
    }

    private fun setupCategoryRecyclerView() {
        categoryList = ArrayList()
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        categoryAdapter.setOnCategoryClickListener(object : CategoryAdapter.OnCategoryClickListener {
            override fun onCategoryClick(category: Category) {
                fetchMoviesByCategory(category)
            }
        })
    }

    private fun observeCategories() {
        homeViewModel.getCategories().observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                categoryList = categories
                categoryAdapter.updateCategories(categories)
            } else {
                showError("Error loading categories")
            }
        })
    }

    private fun observeMovies() {
        homeViewModel.getMovies().observe(viewLifecycleOwner, Observer { movies ->
            if (movies != null) {
                movieAdapter.updateMovies(movies)
            } else {
                showError("Error loading movies")
            }
        })
    }

    private fun fetchMoviesByCategory(category: Category) {
        homeViewModel.fetchMoviesByCategory(category)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}

// ViewModel to handle Home Fragment logic
package com.netflixclone.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netflixclone.app.models.Movie
import com.netflixclone.app.models.Category
import com.netflixclone.app.services.ApiService

class HomeViewModel : ViewModel() {

    private val categoriesLiveData: MutableLiveData<List<Category>> = MutableLiveData()
    private val moviesLiveData: MutableLiveData<List<Movie>> = MutableLiveData()

    init {
        loadCategories()
        loadMovies()
    }

    fun getCategories(): LiveData<List<Category>> {
        return categoriesLiveData
    }

    fun getMovies(): LiveData<List<Movie>> {
        return moviesLiveData
    }

    private fun loadCategories() {
        val categories = ApiService.getCategories()
        categoriesLiveData.postValue(categories)
    }

    private fun loadMovies() {
        val movies = ApiService.getMovies()
        moviesLiveData.postValue(movies)
    }

    fun fetchMoviesByCategory(category: Category) {
        val movies = ApiService.getMoviesByCategory(category)
        moviesLiveData.postValue(movies)
    }
}

// Adapter for Category RecyclerView
package com.netflixclone.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.models.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private var categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var listener: OnCategoryClickListener? = null

    fun setOnCategoryClickListener(listener: OnCategoryClickListener) {
        this.listener = listener
    }

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            itemView.categoryName.text = category.name
            itemView.setOnClickListener {
                listener?.onCategoryClick(category)
            }
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}

// Adapter for Movie RecyclerView
package com.netflixclone.app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.activities.MovieDetailActivity
import com.netflixclone.app.models.Movie
import kotlinx.android.synthetic.main.item_movie.view.*

class MovieAdapter(private var movies: List<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            itemView.movieTitle.text = movie.title
            itemView.movieDescription.text = movie.description
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MovieDetailActivity::class.java)
                intent.putExtra("movieId", movie.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}
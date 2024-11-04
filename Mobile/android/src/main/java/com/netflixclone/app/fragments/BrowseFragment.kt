package com.netflixclone.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.app.R
import com.netflixclone.app.adapters.MovieAdapter
import com.netflixclone.app.models.Movie
import com.netflixclone.app.services.ApiService
import com.netflixclone.app.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrowseFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private var movieList: MutableList<Movie> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_browse, container, false)

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorTextView)

        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Initialize adapter and set to RecyclerView
        movieAdapter = MovieAdapter(movieList)
        recyclerView.adapter = movieAdapter

        // Fetch Movies
        fetchMovies()

        return view
    }

    private fun fetchMovies() {
        // Display progress bar while fetching data
        progressBar.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            // Handle no network connection case
            progressBar.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = getString(R.string.no_network_connection)
            return
        }

        // Make API call to fetch movies
        val apiService = ApiService.create()
        val call: Call<List<Movie>> = apiService.getMovies()

        call.enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    movieList.clear()
                    movieList.addAll(response.body()!!)
                    movieAdapter.notifyDataSetChanged()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    handleApiError()
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                progressBar.visibility = View.GONE
                handleApiError()
            }
        })
    }

    private fun handleApiError() {
        recyclerView.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = getString(R.string.failed_to_load_movies)
    }
}
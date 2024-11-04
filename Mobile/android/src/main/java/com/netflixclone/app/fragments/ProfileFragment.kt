package com.netflixclone.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.netflixclone.app.R
import com.netflixclone.app.activities.LoginActivity
import com.netflixclone.app.activities.MainActivity
import com.netflixclone.app.models.User
import com.netflixclone.app.services.AuthService
import com.netflixclone.app.utils.NetworkUtils
import com.netflixclone.app.viewmodels.UserViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var authService: AuthService
    private lateinit var userViewModel: UserViewModel
    private lateinit var userImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        userImageView = rootView.findViewById(R.id.userImageView)
        userNameTextView = rootView.findViewById(R.id.userNameTextView)
        userEmailTextView = rootView.findViewById(R.id.userEmailTextView)
        logoutButton = rootView.findViewById(R.id.logoutButton)

        authService = AuthService(requireContext())
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        logoutButton.setOnClickListener {
            handleLogout()
        }

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            loadUserProfile()
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }

    private fun loadUserProfile() {
        val currentUser = authService.getCurrentUser()
        currentUser?.let {
            userViewModel.getUserData(it.id).observe(viewLifecycleOwner, Observer { user ->
                updateUI(user)
            })
        } ?: run {
            redirectToLogin()
        }
    }

    private fun updateUI(user: User) {
        userNameTextView.text = user.name
        userEmailTextView.text = user.email

        // Load user profile image using Picasso
        Picasso.get()
            .load(user.profileImage)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(userImageView)
    }

    private fun handleLogout() {
        authService.logout()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
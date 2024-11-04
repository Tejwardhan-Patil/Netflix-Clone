package com.netflixclone.app.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.formatDate(inputFormat: String, outputFormat: String): String {
    return try {
        val sdf = SimpleDateFormat(inputFormat, Locale.getDefault())
        val date = sdf.parse(this) ?: return this
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        output.format(date)
    } catch (e: Exception) {
        Log.e("Extensions", "Error formatting date", e)
        this
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    requireContext().toast(message)
}

fun ImageView.loadImage(url: String) {
    Glide.with(this.context).load(url).into(this)
}

fun RecyclerView.clearAll() {
    this.adapter = null
}

fun <T> List<T>.toFormattedString(): String {
    return joinToString(", ")
}

fun Int.isEven(): Boolean {
    return this % 2 == 0
}

fun Int.isOdd(): Boolean {
    return this % 2 != 0
}

fun Long.formatToReadableDate(): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}

inline fun <reified T : Any> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.block()
    ContextCompat.startActivity(this, intent, null)
}

fun String.isNumeric(): Boolean {
    return this.matches(Regex("-?\\d+(\\.\\d+)?"))
}

fun List<String>.filterNonEmpty(): List<String> {
    return this.filter { it.isNotEmpty() }
}

fun Fragment.navigateTo(destination: Class<*>) {
    val intent = Intent(requireContext(), destination)
    startActivity(intent)
}

fun Int.toFormattedString(): String {
    return String.format("%,d", this)
}

fun Double.formatToCurrency(): String {
    return String.format("$%,.2f", this)
}

fun String.containsIgnoreCase(other: String): Boolean {
    return this.lowercase(Locale.getDefault()).contains(other.lowercase(Locale.getDefault()))
}

fun View.setVisibleOrGone(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.limitLength(maxLength: Int): String {
    return if (this.length > maxLength) this.substring(0, maxLength) + "..." else this
}

fun Long.formatTimeAgo(): String {
    val diff = System.currentTimeMillis() - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        else -> "$days days ago"
    }
}

fun View.disable() {
    this.isEnabled = false
}

fun View.enable() {
    this.isEnabled = true
}

fun String.isValidPassword(): Boolean {
    return this.length >= 8 && this.any { it.isDigit() } && this.any { it.isUpperCase() }
}

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun List<String>.concatenate(): String {
    return this.joinToString(separator = ", ")
}

fun String?.orEmptyOrDefault(default: String = "N/A"): String {
    return this?.takeIf { it.isNotEmpty() } ?: default
}

fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Double.roundTo(decimals: Int): Double {
    val factor = Math.pow(10.0, decimals.toDouble())
    return Math.round(this * factor) / factor
}

fun String.reverse(): String {
    return this.reversed()
}

fun Boolean.toVisibility(): Int {
    return if (this) View.VISIBLE else View.GONE
}

fun View.setVisibility(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.toSlug(): String {
    return this.trim().lowercase(Locale.getDefault()).replace(" ", "-")
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = android.net.Uri.parse(url)
    }
    startActivity(intent)
}

fun String.removeWhitespace(): String {
    return this.replace("\\s+".toRegex(), "")
}
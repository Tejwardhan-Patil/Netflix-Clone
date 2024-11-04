package com.netflixclone.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

object NetworkUtils {

    /**
     * Checks if the device is connected to a network
     * @param context Application context
     * @return true if connected to a network, false otherwise
     */
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        }
    }

    /**
     * Returns the type of network connected (Wi-Fi, Cellular, or None)
     * @param context Application context
     * @return Network type as a String (Wi-Fi, Cellular, None)
     */
    fun getNetworkType(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return "None"
            val capabilities = cm.getNetworkCapabilities(network) ?: return "None"
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                else -> "None"
            }
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            when (activeNetwork?.type) {
                ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
                ConnectivityManager.TYPE_MOBILE -> "Cellular"
                else -> "None"
            }
        }
    }

    /**
     * Checks if the device is connected to a Wi-Fi network
     * @param context Application context
     * @return true if connected to Wi-Fi, false otherwise
     */
    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.type == ConnectivityManager.TYPE_WIFI
        }
    }

    /**
     * Checks if the device is connected to a cellular network
     * @param context Application context
     * @return true if connected to cellular, false otherwise
     */
    fun isCellularConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.type == ConnectivityManager.TYPE_MOBILE
        }
    }

    /**
     * Returns the name of the connected Wi-Fi network
     * @param context Application context
     * @return Wi-Fi SSID as a string
     */
    fun getWifiSSID(context: Context): String? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return null
            val capabilities = cm.getNetworkCapabilities(network) ?: return null
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                wifiManager.connectionInfo.ssid
            } else null
        } else {
            null
        }
    }

    /**
     * Returns the signal strength of the current network
     * @param context Application context
     * @return Signal strength as a percentage
     */
    fun getSignalStrength(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return 0
            val capabilities = cm.getNetworkCapabilities(network) ?: return 0
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                val rssi = wifiManager.connectionInfo.rssi
                val level = android.net.wifi.WifiManager.calculateSignalLevel(rssi, 100)
                level
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val cellSignalStrength = telephonyManager.signalStrength
                cellSignalStrength?.level ?: 0
            } else 0
        } else {
            0
        }
    }

    /**
     * Checks if there is any network restriction or a VPN connection
     * @param context Application context
     * @return true if there is a VPN connection, false otherwise
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isVpnConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    /**
     * Checks if the device is roaming on a cellular network
     * @param context Application context
     * @return true if roaming, false otherwise
     */
    fun isRoaming(context: Context): Boolean {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.isNetworkRoaming
    }
    
    /**
     * Fetches the network subtype (EDGE, LTE)
     * @param context Application context
     * @return Network subtype as a String
     */
    fun getNetworkSubtype(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (tm.networkType) {
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            else -> "Unknown"
        }
    }
}
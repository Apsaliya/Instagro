package com.ankit.connect.extensions

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import com.ankit.connect.util.Constants.PREF_NAME

/**
 * Created by ankit on 13/04/18.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.hasInternetConnection(): Boolean {
  val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
  val activeNetwork = cm.activeNetworkInfo
  return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

inline fun Context.getPreference() = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

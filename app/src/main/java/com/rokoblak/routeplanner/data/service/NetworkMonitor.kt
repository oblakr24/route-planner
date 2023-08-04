package com.rokoblak.routeplanner.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface NetworkMonitor {
    val connected: Flow<Boolean>
}

class AppNetworkMonitor @Inject constructor(@ApplicationContext context: Context): NetworkMonitor {

    private val _connected = MutableStateFlow(true)
    override val connected = _connected

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onNetworkChanged(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onNetworkChanged(false)
            }
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        val status = connectivityManager.activeNetwork.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) ?: false
        }

        onNetworkChanged(status)
    }

    private fun onNetworkChanged(available: Boolean) {
        _connected.value = available
    }
}

package io.github.sami00777.connectivity_monitoring

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkCallback(
    private val connectivityManager: ConnectivityManager
) {

    fun networkStateFlow(): Flow<ConnectionState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val connectionState = determineConnectionState(networkCapabilities)
                trySend(connectionState)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectionState.Disconnected)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val connectionState = determineConnectionState(networkCapabilities)
                trySend(connectionState)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        val initialState = getCurrentConnectionState()
        trySend(initialState)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }

    }.distinctUntilChanged()

    private fun getCurrentConnectionState(): ConnectionState {
        val activeNetwork = connectivityManager.activeNetwork ?: return ConnectionState.Disconnected
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return determineConnectionState(networkCapabilities)
    }


    private fun determineConnectionState(networkCapabilities: NetworkCapabilities?): ConnectionState {
        return when {
            networkCapabilities == null -> ConnectionState.Unknown

            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> {
                ConnectionState.Connected
            }

            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                ConnectionState.ConnectedWithoutInternet
            }

            else -> ConnectionState.Disconnected
        }
    }
}
package io.github.sami00777.connectivity_monitoring

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Monitors network connectivity and exposes the current connection state.
 *
 * Usage:
 * ```
 * val connectivityMonitor = ConnectivityMonitor(context)
 *
 * // Observe connection state
 * connectivityMonitor.connectionState.collect { state ->
 *     when (state) {
 *         is ConnectionState.Connected -> // Handle online state
 *         is ConnectionState.Disconnected -> // Handle offline state
 *         is ConnectionState.ConnectedWithoutInternet -> // Handle limited connectivity
 *         is ConnectionState.Unknown -> // Handle unknown state
 *     }
 * }
 *
 * // Or simply check if online
 * connectivityMonitor.isOnline.collect { isOnline ->
 *     // Handle online/offline state
 * }
 * ```
 */

class ConnectivityMonitor(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = NetworkCallback(connectivityManager)
    private val scope = CoroutineScope(SupervisorJob())

    /**
     * StateFlow that emits the current connection state.
     * The flow is hot and will immediately emit the current state when collected.
     */
    val connectionState: StateFlow<ConnectionState> = networkCallback
        .networkStateFlow()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectionState.Unknown
        )

    val isOnline: StateFlow<Boolean> = connectionState
        .map { it.isOnline }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Get the current connection state synchronously.
     * Note: This might return Unknown if the state hasn't been determined yet.
     */
    fun getCurrentConnectionState(): ConnectionState = connectionState.value

    /**
     * Get the current online status synchronously.
     */
    fun isCurrentlyOnline(): Boolean = isOnline.value

}
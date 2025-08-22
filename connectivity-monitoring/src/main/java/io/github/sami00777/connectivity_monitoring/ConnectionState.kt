package io.github.sami00777.connectivity_monitoring

/**
 * Represents the current network connectivity state of the device.
 */
sealed class ConnectionState {
    /**
     * Device is connected to a network and has internet access.
     */
    object Connected : ConnectionState()

    /**
     * Device is not connected to any network.
     */
    object Disconnected : ConnectionState()

    /**
     * Device is connected to a network but internet access is unavailable.
     */
    object ConnectedWithoutInternet : ConnectionState()

    /**
     * Connectivity state is unknown or being determined.
     */
    object Unknown : ConnectionState()
}

/**
 * Extension property to easily check if the connection state represents an online state.
 */
val ConnectionState.isOnline: Boolean
    get() = this is ConnectionState.Connected
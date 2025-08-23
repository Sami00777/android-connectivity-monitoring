# Connectivity Monitor Library

A lightweight Android library that monitors network connectivity and exposes the device's network state as Kotlin `StateFlow`.

## Features

- 🔄 Real-time network connectivity monitoring
- 🌊 Reactive `StateFlow` API
- 🎯 Distinguishes between connected, disconnected, and limited connectivity states
- 📱 Modern Kotlin Coroutines support
- 🧪 Well-tested and reliable
- 📦 Lightweight with minimal dependencies

# Connectivity Monitoring Library

[![Maven Central]([https://img.shields.io/maven-central/v/io.github.sami00777/connectivity-monitoring)](https://central.sonatype.com/artifact/io.github.sami00777/connectivity-monitoring](https://central.sonatype.com/artifact/io.github.sami00777/connectivity-monitoring/overview))

## Installation

### Add to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.sami00777:connectivity-monitoring:1.0.2")
}
```

### Required Permissions

The library automatically includes the required permission in its manifest:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Usage

### Basic Usage

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var connectivityMonitor: ConnectivityMonitor
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the connectivity monitor
        connectivityMonitor = ConnectivityMonitor(this)
        
        // Collect connection state
        lifecycleScope.launch {
            connectivityMonitor.connectionState.collect { state ->
                when (state) {
                    is ConnectionState.Connected -> {
                        // Device is online with validated internet access
                        showOnlineUI()
                    }
                    is ConnectionState.Disconnected -> {
                        // Device is not connected to any network
                        showOfflineUI()
                    }
                    is ConnectionState.ConnectedWithoutInternet -> {
                        // Device is connected to network but no internet access
                        showLimitedConnectivityUI()
                    }
                    is ConnectionState.Unknown -> {
                        // Connection state is being determined
                        showLoadingUI()
                    }
                }
            }
        }
    }
}
```

### Simple Online/Offline Monitoring

```kotlin
// Just check if online or offline
lifecycleScope.launch {
    connectivityMonitor.isOnline.collect { isOnline ->
        if (isOnline) {
            // Device has internet access
            enableOnlineFeatures()
        } else {
            // Device is offline or has limited connectivity
            enableOfflineMode()
        }
    }
}
```

### ViewModel Integration

```kotlin
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val connectivityMonitor = ConnectivityMonitor(application)
    
    val connectionState = connectivityMonitor.connectionState
    val isOnline = connectivityMonitor.isOnline
    
    fun performNetworkOperation() {
        if (connectivityMonitor.isCurrentlyOnline()) {
            // Perform network operation
        } else {
            // Handle offline state
        }
    }
}
```

### Repository Pattern

```kotlin
class DataRepository(private val connectivityMonitor: ConnectivityMonitor) {
    
    suspend fun getData(): Result<Data> {
        return when (connectivityMonitor.connectionState.value) {
            is ConnectionState.Connected -> {
                // Fetch from network
                fetchFromNetwork()
            }
            else -> {
                // Fetch from cache or return error
                fetchFromCache()
            }
        }
    }
}
```

## API Reference

### ConnectivityMonitor

Main class for monitoring network connectivity.

**Constructor:**
- `ConnectivityMonitor(context: Context)`

**Properties:**
- `connectionState: StateFlow<ConnectionState>` - Emits current connection state
- `isOnline: StateFlow<Boolean>` - Emits true when device has validated internet access

**Methods:**
- `getCurrentConnectionState(): ConnectionState` - Get current state synchronously
- `isCurrentlyOnline(): Boolean` - Get current online status synchronously

### ConnectionState

Sealed class representing network connectivity states:

- `ConnectionState.Connected` - Device has validated internet access
- `ConnectionState.Disconnected` - Device is not connected to any network
- `ConnectionState.ConnectedWithoutInternet` - Device is connected but no internet access
- `ConnectionState.Unknown` - Connection state is being determined

**Extensions:**
- `ConnectionState.isOnline: Boolean` - True only for `Connected` state

## Requirements

- Android API 24+ (Android 7.0)
- Kotlin 1.8+
- Coroutines support

## License

```
Copyright 2024 Xanroid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

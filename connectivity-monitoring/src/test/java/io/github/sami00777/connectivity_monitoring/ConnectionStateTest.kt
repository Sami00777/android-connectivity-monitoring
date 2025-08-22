package io.github.sami00777.connectivity_monitoring

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectionStateTest {

    @Test
    fun `Connected state should be online`() {
        val state = ConnectionState.Connected
        assertTrue(state.isOnline)
    }

    @Test
    fun `Disconnected state should not be online`() {
        val state = ConnectionState.Disconnected
        assertFalse(state.isOnline)
    }

    @Test
    fun `ConnectedWithoutInternet state should not be online`() {
        val state = ConnectionState.ConnectedWithoutInternet
        assertFalse(state.isOnline)
    }

    @Test
    fun `Unknown state should not be online`() {
        val state = ConnectionState.Unknown
        assertFalse(state.isOnline)
    }

    @Test
    fun `ConnectionState sealed class hierarchy should be correct`() {
        val states = listOf(
            ConnectionState.Connected,
            ConnectionState.Disconnected,
            ConnectionState.ConnectedWithoutInternet,
            ConnectionState.Unknown
        )
        states.forEach { state ->
            assertTrue("$state should be an instance of ConnectionState", state is ConnectionState)
        }
    }
}
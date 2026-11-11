package com.hypercapsule.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying package allow-list enable and disable toggling logic.
 * Ensures bidirectional ON -> OFF and OFF -> ON transitions work reliably.
 */
class PackageToggleTest {

    private fun Set<String>.serialize(): String = this.joinToString(",")
    private fun String?.deserializeSet(): Set<String> =
        this?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()

    private fun togglePackage(currentSerialized: String?, packageName: String, isEnabled: Boolean): String {
        val currentSet = currentSerialized.deserializeSet()
        val newSet = if (isEnabled) currentSet + packageName else currentSet - packageName
        return newSet.serialize()
    }

    @Test
    fun testEnablePackageFromEmpty() {
        val initial = ""
        val afterEnable = togglePackage(initial, "com.whatsapp", true)
        val set = afterEnable.deserializeSet()

        assertTrue(set.contains("com.whatsapp"))
        assertEquals(1, set.size)
    }

    @Test
    fun testDisablePackageAfterEnabled() {
        val initial = "com.whatsapp,com.spotify.music"
        val afterDisable = togglePackage(initial, "com.whatsapp", false)
        val set = afterDisable.deserializeSet()

        assertFalse(set.contains("com.whatsapp"))
        assertTrue(set.contains("com.spotify.music"))
        assertEquals(1, set.size)
    }

    @Test
    fun testRepeatedToggleCycles() {
        var state = ""

        // Cycle 1: Enable WhatsApp
        state = togglePackage(state, "com.whatsapp", true)
        assertTrue(state.deserializeSet().contains("com.whatsapp"))

        // Cycle 2: Disable WhatsApp
        state = togglePackage(state, "com.whatsapp", false)
        assertFalse(state.deserializeSet().contains("com.whatsapp"))

        // Cycle 3: Re-enable WhatsApp
        state = togglePackage(state, "com.whatsapp", true)
        assertTrue(state.deserializeSet().contains("com.whatsapp"))

        // Cycle 4: Add second app (Spotify)
        state = togglePackage(state, "com.spotify.music", true)
        assertTrue(state.deserializeSet().contains("com.whatsapp"))
        assertTrue(state.deserializeSet().contains("com.spotify.music"))

        // Cycle 5: Disable Spotify, keep WhatsApp
        state = togglePackage(state, "com.spotify.music", false)
        assertTrue(state.deserializeSet().contains("com.whatsapp"))
        assertFalse(state.deserializeSet().contains("com.spotify.music"))

        // Cycle 6: Disable WhatsApp, state is now empty
        state = togglePackage(state, "com.whatsapp", false)
        assertTrue(state.deserializeSet().isEmpty())
    }
}

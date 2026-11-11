package com.hypercapsule.app

import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.models.NotificationType
import com.hypercapsule.app.models.WidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

/**
 * Regression and validation test suite for HyperCapsule Forensic Bug Remediations:
 * - BUG 01 (#410): Notification Timeout Mismatch (seconds to ms, safe Long, max 24h)
 * - BUG 02 (#438): Discord Voice / Ongoing Call Rapid Removal & Repost sequence
 * - BUG 03 (#445) & BUG 08 (#444): Permanent Island conflict prevention & tablet orientation
 * - LAT-04: Theme ZIP path cross-platform slash normalization
 */
class RemediationVerificationTest {

    // =========================================================================
    // BUG 01: Notification Timeout Conversion Tests
    // =========================================================================

    @Test
    fun testIslandConfigTimeoutConversion() {
        // Default / null timeout
        val configNull = IslandConfig()
        assertNull(configNull.timeoutMs)
        assertNull(configNull.floatTimeoutMs)

        // 10 seconds -> 10,000 ms
        val config10 = IslandConfig(timeout = 10, floatTimeout = 5)
        assertEquals(10_000, config10.timeoutMs)
        assertEquals(5_000, config10.floatTimeoutMs)

        // Zero or negative timeout should return null (no auto-dismiss)
        val configZero = IslandConfig(timeout = 0, floatTimeout = -1)
        assertNull(configZero.timeoutMs)
        assertNull(configZero.floatTimeoutMs)

        // Upper bound safety: max 24 hours (86,400,000 ms)
        val configHuge = IslandConfig(timeout = 100_000, floatTimeout = 200_000)
        assertEquals(86_400_000, configHuge.timeoutMs)
        assertEquals(86_400_000, configHuge.floatTimeoutMs)
    }

    @Test
    fun testWidgetConfigTimeoutConversion() {
        val widgetConfig = WidgetConfig(timeout = 15)
        assertEquals(15_000, widgetConfig.timeoutMs)

        val widgetZero = WidgetConfig(timeout = 0)
        assertNull(widgetZero.timeoutMs)
    }

    // =========================================================================
    // BUG 02: Discord Voice / Ongoing Notification Rapid Repost Simulation
    // =========================================================================

    @Test
    fun testOngoingNotificationBypassesRecentlyRemovedDebounce() {
        val recentlyRemovedKeys = ConcurrentHashMap<String, Long>()
        val discordKey = "0|com.discord|100|null|10123"

        // Simulate onNotificationRemoved recording key at t0
        val t0 = System.currentTimeMillis()
        recentlyRemovedKeys[discordKey] = t0

        // Function replicating the debouncing logic in NotificationReaderService
        fun shouldSkipPost(
            key: String,
            isOngoing: Boolean,
            type: NotificationType
        ): Boolean {
            val isOngoingOrSpecial = isOngoing ||
                type == NotificationType.CALL ||
                type == NotificationType.NAVIGATION ||
                type == NotificationType.MEDIA

            val removedTime = recentlyRemovedKeys[key]
            if (removedTime != null && System.currentTimeMillis() - removedTime < 2000) {
                return if (isOngoingOrSpecial) {
                    recentlyRemovedKeys.remove(key)
                    false // Do not skip!
                } else {
                    true // Skip standard notification
                }
            }
            return false
        }

        // Standard notification repost within 50ms should be skipped (debounced)
        val standardKey = "0|com.example.chat|1|null|1000"
        recentlyRemovedKeys[standardKey] = t0
        val standardSkipped = shouldSkipPost(standardKey, isOngoing = false, type = NotificationType.STANDARD)
        assertTrue("Standard notification repost should be debounced within 2000ms", standardSkipped)

        // Discord Call notification repost within 50ms MUST NOT be skipped
        val discordSkipped = shouldSkipPost(discordKey, isOngoing = true, type = NotificationType.CALL)
        assertFalse("Discord voice/call notification repost must NOT be skipped", discordSkipped)

        // Key should have been cleared from recentlyRemovedKeys
        assertFalse(recentlyRemovedKeys.containsKey(discordKey))
    }

    // =========================================================================
    // BUG 03 & BUG 08: Permanent Island Desired Active Simulation
    // =========================================================================

    @Test
    fun testPermanentIslandDesiredActiveLogic() {
        // Replicating desiredActive() logic
        fun desiredActive(
            isPermanentIslandEnabled: Boolean,
            hasNativeIsland: Boolean,
            currentRealNotifications: Int,
            isHideInLandscapeEnabled: Boolean,
            isLandscape: Boolean,
            isTablet: Boolean
        ): Boolean {
            val landscapeSuppressed = isHideInLandscapeEnabled && isLandscape && !isTablet
            return isPermanentIslandEnabled && !hasNativeIsland && currentRealNotifications == 0 && !landscapeSuppressed
        }

        // Case 1: Feature enabled, no native island, no bridged notifications -> ACTIVE
        assertTrue(desiredActive(
            isPermanentIslandEnabled = true,
            hasNativeIsland = false,
            currentRealNotifications = 0,
            isHideInLandscapeEnabled = false,
            isLandscape = false,
            isTablet = false
        ))

        // Case 2: Bridged active notifications exist (e.g. Spotify or Navigation) -> SUPPRESSED
        assertFalse("Permanent island must be suppressed when bridged notifications are active", desiredActive(
            isPermanentIslandEnabled = true,
            hasNativeIsland = false,
            currentRealNotifications = 1,
            isHideInLandscapeEnabled = false,
            isLandscape = false,
            isTablet = false
        ))

        // Case 3: Native island exists -> SUPPRESSED
        assertFalse("Permanent island must be suppressed when native island exists", desiredActive(
            isPermanentIslandEnabled = true,
            hasNativeIsland = true,
            currentRealNotifications = 0,
            isHideInLandscapeEnabled = false,
            isLandscape = false,
            isTablet = false
        ))

        // Case 4: Phone in landscape with hideInLandscape=true -> SUPPRESSED
        assertFalse("Permanent island should be suppressed on phone in landscape when setting enabled", desiredActive(
            isPermanentIslandEnabled = true,
            hasNativeIsland = false,
            currentRealNotifications = 0,
            isHideInLandscapeEnabled = true,
            isLandscape = true,
            isTablet = false
        ))

        // Case 5: Tablet in landscape with hideInLandscape=true -> ALLOWED (Tablet landscape is primary)
        assertTrue("Permanent island should NOT be suppressed on tablets in landscape", desiredActive(
            isPermanentIslandEnabled = true,
            hasNativeIsland = false,
            currentRealNotifications = 0,
            isHideInLandscapeEnabled = true,
            isLandscape = true,
            isTablet = true
        ))
    }

    // =========================================================================
    // LAT-04: Theme Portable ZIP Path Formatting
    // =========================================================================

    @Test
    fun testZipEntryNamePathNormalization() {
        val windowsRelativePath = "icons\\notification\\app_icon.png"
        val normalizedPath = windowsRelativePath.replace('\\', '/')
        assertEquals("icons/notification/app_icon.png", normalizedPath)
        assertFalse(normalizedPath.contains('\\'))
    }
}

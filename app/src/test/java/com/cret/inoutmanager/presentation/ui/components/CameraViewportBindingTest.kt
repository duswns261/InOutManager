package com.cret.inoutmanager.presentation.ui.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraViewportBindingTest {

    @Test
    fun `zero width or height is not bind ready`() {
        assertFalse(isCameraBindReady(width = 0, height = 0))
        assertFalse(isCameraBindReady(width = 0, height = 1080))
        assertFalse(isCameraBindReady(width = 1080, height = 0))
    }

    @Test
    fun `positive width and height is bind ready`() {
        assertTrue(isCameraBindReady(width = 1080, height = 1920))
    }

    @Test
    fun `first bind with no previous key always rebinds`() {
        val current = CameraBindKey(width = 1080, height = 1920, rotation = 0)

        assertTrue(shouldRebindCamera(previous = null, current = current))
    }

    @Test
    fun `same width height and rotation does not rebind`() {
        val key = CameraBindKey(width = 1080, height = 1920, rotation = 0)

        assertFalse(shouldRebindCamera(previous = key, current = key))
    }

    @Test
    fun `size change from rotation triggers rebind`() {
        val previous = CameraBindKey(width = 1080, height = 1920, rotation = 0)
        val current = CameraBindKey(width = 1920, height = 1080, rotation = 1)

        assertTrue(shouldRebindCamera(previous = previous, current = current))
    }

    @Test
    fun `rotation change alone triggers rebind`() {
        val previous = CameraBindKey(width = 1080, height = 1920, rotation = 0)
        val current = CameraBindKey(width = 1080, height = 1920, rotation = 2)

        assertTrue(shouldRebindCamera(previous = previous, current = current))
    }
}

package com.aegisdev.ktreesitter

object NativeLoader {
    private var areLibrariesLoaded = false
    private val lock = Any()

    fun load() {
        synchronized(lock) {
            if (areLibrariesLoaded) return
            try {
                System.loadLibrary("ktreesitter")
                System.loadLibrary("java-grammar")
                System.loadLibrary("python-grammar")
                areLibrariesLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                throw IllegalStateException("Failed to load native KTreeSitter libraries.", e)
            }
        }
    }
}
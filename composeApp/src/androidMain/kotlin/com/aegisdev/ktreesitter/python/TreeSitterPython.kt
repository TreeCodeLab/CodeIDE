package com.aegisdev.ktreesitter.python

// The actual implementation for loading the Python grammar on Android.
object TreeSitterPython {
    init {
        // This loads the libpython-grammar.so file that our CMakeLists.txt builds.
        System.loadLibrary("python-grammar")
    }

    @JvmName("getLanguagePointer")
    private external fun getLanguagePointer(): Long

    /**
     * Allows us to call the object like a function: TreeSitterPython()
     * It calls our external JNI function and returns the pointer as a Long.
     */
    operator fun invoke(): Any = getLanguagePointer()
}
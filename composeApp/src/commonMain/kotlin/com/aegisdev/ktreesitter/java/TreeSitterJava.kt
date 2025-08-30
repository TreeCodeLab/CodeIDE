package com.aegisdev.ktreesitter.java

// This declares a promise that each platform (like Android)
// will provide an 'actual' implementation of this object.
expect object TreeSitterJava {
    /**
     * The 'operator fun invoke' allows us to call the object like a function:
     * val pointer = TreeSitterJava()
     *
     * It promises to return 'Any', which will be a 'Long' on JVM/Android
     * and a 'CPointer' on native targets.
     */
    operator fun invoke(): Any
}
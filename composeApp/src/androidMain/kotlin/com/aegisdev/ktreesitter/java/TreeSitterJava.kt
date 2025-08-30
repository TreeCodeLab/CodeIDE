package com.aegisdev.ktreesitter.java

// This is the ACTUAL implementation for the Android target.
actual object TreeSitterJava {
    init {
        // This loads the libjava-grammar.so file that our CMakeLists.txt builds.
        System.loadLibrary("java-grammar")
    }

    /**
     * This declares a function that will be provided by a native (JNI) library.
     * The JVM will look for a C function named:
     * Java_com_aegisdev_ktreesitter_java_TreeSitterJava_getLanguagePointer
     * This perfectly matches the function in our jni-bridge.c file.
     */
    @JvmName("getLanguagePointer")
    private external fun getLanguagePointer(): Long

    /**
     * This fulfills the 'expect' contract from commonMain.
     * When code in MainActivity calls TreeSitterJava(), this is the code that runs.
     * It calls our external JNI function and returns the pointer as a Long,
     * which is what the KTreeSitter 'Language' class constructor expects.
     */
    actual operator fun invoke(): Any = getLanguagePointer()
}
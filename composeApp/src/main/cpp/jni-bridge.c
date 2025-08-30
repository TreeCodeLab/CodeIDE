#include <jni.h>
#include "tree_sitter/api.h"

// This tells the C compiler that a function named `tree_sitter_java`
// exists in another file (the grammar's parser.c) and returns a TSLanguage pointer.
const TSLanguage *tree_sitter_java();

/*
 * This is the JNI function that Kotlin will call.
 * The name is constructed automatically by the JVM based on your Kotlin file:
 * Java_ + package_name_with_underscores + _ + ClassName + _ + functionName
 */
JNIEXPORT jlong JNICALL
Java_com_aegisdev_ktreesitter_java_TreeSitterJava_getLanguagePointer(JNIEnv *env, jobject thiz) {
    // We simply call the real C function and return its result (the pointer) as a long.
    return (jlong)tree_sitter_java();
}
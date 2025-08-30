#include <jni.h>
#include "tree_sitter/api.h"

// This function is defined in the grammar's parser.c
const TSLanguage *tree_sitter_python();

/*
 * JNI function that Kotlin will call for the Python grammar.
 * The name matches the new Kotlin package and object.
 */
JNIEXPORT jlong JNICALL
Java_com_aegisdev_ktreesitter_python_TreeSitterPython_getLanguagePointer(JNIEnv *env, jobject thiz) {
    return (jlong)tree_sitter_python();
}
# This is the definitive build script for Windows, macOS, and Linux.
# It creates one core shared library and links the smaller grammar
# libraries against it, preventing all symbol collision errors.
cmake_minimum_required(VERSION 3.22.1)
project("ktreesitter-app")

# --- PART 1: Build the ONE Core Engine Library ---
# We define "ktreesitter" as a SHARED library. This creates a single,
# reusable "ktreesitter.dll" (on Windows) or "libktreesitter.so" file.
# It contains the JNI_OnLoad function and the entire tree-sitter runtime exactly once.
add_library(ktreesitter SHARED
        ktreesitter/jni/language.c
        ktreesitter/jni/lookahead_iterator.c
        ktreesitter/jni/node.c
        ktreesitter/jni/parser.c
        ktreesitter/jni/query.c
        ktreesitter/jni/tree.c
        ktreesitter/jni/tree_cursor.c
        ktreesitter/jni/module.c
        tree-sitter/src/lib.c
)

# Tell this library where to find its own header files.
target_include_directories(ktreesitter PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/tree-sitter/include
        ${CMAKE_CURRENT_SOURCE_DIR}/tree-sitter/src
        ${CMAKE_CURRENT_SOURCE_DIR}/ktreesitter/jni
)

# --- PART 2: Build the Java Grammar "Adapter" Library ---
# This library is now very small. It only contains the Java-specific code.
# It does NOT contain a copy of the core engine.
add_library(java-grammar SHARED
        jni-bridge.c
        # The path is now simple and relative because the grammars folder is inside cpp/
        grammars/tree-sitter-java/src/parser.c
)

# Tell this library where to find the main tree-sitter headers.
target_include_directories(java-grammar PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/tree-sitter/include
        grammars/tree-sitter-java/src
)

# This is the critical link. We tell the build system that "java-grammar"
# REQUIRES "ktreesitter" to function.
target_link_libraries(java-grammar PRIVATE ktreesitter)

# --- PART 3: Build the Python Grammar "Adapter" Library ---
# Same as above for Python. This library is also small and does not
# contain a copy of the core engine.
add_library(python-grammar SHARED
        jni-bridge-python.c
        # The path is now simple and relative.
        grammars/tree-sitter-python/src/parser.c
        grammars/tree-sitter-python/src/scanner.c
)

# Tell this library where to find the main tree-sitter headers.
target_include_directories(python-grammar PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/tree-sitter/include
        grammars/tree-sitter-python/src
)

# Link the Python adapter to the one, shared core engine.
target_link_libraries(python-grammar PRIVATE ktreesitter)
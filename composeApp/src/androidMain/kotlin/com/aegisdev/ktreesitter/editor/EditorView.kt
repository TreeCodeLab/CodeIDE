package com.aegisdev.ktreesitter.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.aegisdev.ktreesitter.editor.languages.LanguageManager
import com.aegisdev.ktreesitter.editor.plugin.base.PluginSupplier
import com.aegisdev.ktreesitter.editor.plugin.linenumbers.lineNumbers
import com.aegisdev.ktreesitter.editor.utils.EditorTheme
import com.aegisdev.ktreesitter.editor.widget.TextProcessor

@Composable
fun EditorView(
    modifier: Modifier = Modifier,
    languageManager: LanguageManager,
    languageName: String,
    text: String
) {
    // Remember the TextProcessor instance across recompositions
    val editor = remember {
        // This block is the factory for our editor view. It runs only once.
        TextProcessor(
            context = androidx.compose.ui.platform.LocalContext.current
        ).apply {
            // Apply initial configuration
            colorScheme = EditorTheme.DARCULA
            setTextContent(text)
            language = languageManager.getLanguage(languageName)

            // Install the line numbers plugin from the copied editorkit source
            plugins(PluginSupplier.create {
                lineNumbers {
                    lineNumbers = true
                    highlightCurrentLine = true
                }
            })
        }
    }

    // Use DisposableEffect to ensure resources are cleaned up when the
    // composable leaves the composition.
    DisposableEffect(editor) {
        onDispose {
            // This is crucial for releasing the tree-sitter parser and tree.
            editor.language = null
        }
    }

    // Use AndroidView to embed our modified TextProcessor in the Compose UI.
    AndroidView(
        modifier = modifier,
        factory = { editor },
        update = { view ->
            // This block runs when the inputs to the composable change.
            // It keeps the editor in sync with the state.
            if (view.language?.languageName != languageName) {
                view.language = languageManager.getLanguage(languageName)
            }
            // Avoid resetting text if it hasn't changed, to preserve cursor position.
            if (view.text.toString() != text) {
                view.setTextContent(text)
            }
        }
    )
}
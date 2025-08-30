package com.aegisdev.ktreesitter

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import io.github.treesitter.ktreesitter.Parser
import io.github.treesitter.ktreesitter.Tree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EditorState(
    private val parser: Parser,
    private val coroutineScope: CoroutineScope
) {
    var textFieldValue by mutableStateOf(
        TextFieldValue(
            text = """
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Success!");
                }
            }
            """.trimIndent()
        )
    )
        private set

    private var tree: Tree? = null
    private var highlightingJob: Job? = null
    private val parserMutex = Mutex()

    init {
        // Trigger initial highlighting
        onValueChange(textFieldValue)
    }

    fun onValueChange(newValue: TextFieldValue) {
        textFieldValue = newValue
        highlightingJob?.cancel()
        highlightingJob = coroutineScope.launch(Dispatchers.Default) {
            val newAnnotatedString = parserMutex.withLock {
                val oldTree = tree
                val newTree = parser.parse(newValue.text, oldTree)
                oldTree?.close()
                tree = newTree
                highlight(newText = newValue.text, tree = newTree)
            }
            // Update the state on the main thread
            launch(Dispatchers.Main) {
                textFieldValue = textFieldValue.copy(annotatedString = newAnnotatedString)
            }
        }
    }

    private fun highlight(newText: String, tree: Tree): AnnotatedString {
        return buildAnnotatedString {
            append(newText)
            addStyle(SpanStyle(color = DEFAULT_COLOR), 0, newText.length)
            val textBytes = newText.toByteArray(Charsets.UTF_8)
            val cursor = tree.walk()
            try {
                if (cursor.gotoFirstChild()) {
                    var keepGoing = true
                    while (keepGoing) {
                        val node = cursor.currentNode
                        val nodeType = if (node.isError) "ERROR" else node.type
                        val color = SYNTAX_COLORS[nodeType]
                        if (color != null) {
                            val startByte = node.startByte.toInt()
                            val endByte = node.endByte.toInt()
                            if (startByte < endByte && endByte <= textBytes.size) {
                                val startChar = String(textBytes, 0, startByte, Charsets.UTF_8).length
                                val endChar = String(textBytes, 0, endByte, Charsets.UTF_8).length
                                if (startChar < endChar) {
                                    addStyle(SpanStyle(color = color), startChar, endChar)
                                }
                            }
                        }
                        if (cursor.gotoFirstChild()) continue
                        if (cursor.gotoNextSibling()) continue
                        do {
                            if (!cursor.gotoParent()) {
                                keepGoing = false
                                break
                            }
                        } while (!cursor.gotoNextSibling())
                    }
                }
            } finally {
                cursor.close()
            }
        }
    }

    fun dispose() {
        tree?.close()
    }

    companion object {
        private val SYNTAX_COLORS = mapOf(
            "type_identifier" to Color(0xFF2E8B57), "void_type" to Color(0xFFCF86E8),
            "integral_type" to Color(0xFFCF86E8), "floating_point_type" to Color(0xFFCF86E8),
            "boolean_type" to Color(0xFFCF86E8), "identifier" to Color.White,
            "string_literal" to Color(0xFF6A8759), "comment" to Color(0xFF808080),
            "decimal_integer_literal" to Color(0xFF6897BB), "operator" to Color(0xFFFFCC70),
            "ERROR" to Color.Red
        )
        private val DEFAULT_COLOR = Color(0xFFA9B7C6)
    }
}

@Composable
fun rememberEditorState(parser: Parser): EditorState {
    val coroutineScope = rememberCoroutineScope()
    val state = remember { EditorState(parser, coroutineScope) }
    DisposableEffect(Unit) {
        onDispose {
            state.dispose()
        }
    }
    return state
}
package com.aegisdev.ktreesitter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import io.github.treesitter.ktreesitter.Parser
import io.github.treesitter.ktreesitter.Tree

class SyntaxHighlighter(private val parser: Parser) {
    private var tree: Tree? = null

    fun highlight(text: String): AnnotatedString {
        // This is the crucial lifecycle management part.
        val oldTree = tree
        val newTree = parser.parse(text, oldTree)
        oldTree?.close()
        tree = newTree

        return buildAnnotatedString {
            append(text)
            addStyle(SpanStyle(color = DEFAULT_COLOR), 0, text.length)
            val textBytes = text.toByteArray(Charsets.UTF_8)
            val cursor = newTree.walk()
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

    fun close() {
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
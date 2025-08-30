package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.editor.model.SyntaxHighlightResult
import com.aegisdev.ktreesitter.editor.model.TextStructure
import com.aegisdev.ktreesitter.editor.model.TokenType
import com.aegisdev.ktreesitter.editor.language.base.styler.LanguageStyler
import io.github.treesitter.ktreesitter.Parser
import io.github.treesitter.ktreesitter.Tree

class KTreesitterStyler(
    private val parser: Parser,
    private val typeMap: Map<String, TokenType>
) : LanguageStyler {

    private var lastTree: Tree? = null

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val highlights = mutableListOf<SyntaxHighlightResult>()
        val source = structure.text.toString()
        val oldTree = lastTree
        val newTree = parser.parse(source, oldTree)
        lastTree = newTree
        oldTree?.close()

        val cursor = newTree.walk()
        try {
            if (cursor.gotoFirstChild()) {
                var keepGoing = true
                while (keepGoing) {
                    val node = cursor.currentNode
                    val tokenType = typeMap[node.type]
                    if (tokenType != null) {
                        highlights.add(
                            SyntaxHighlightResult(
                                tokenType,
                                node.startByte.toInt(),
                                node.endByte.toInt()
                            )
                        )
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
        return highlights
    }

    override fun release() {
        parser.close()
        lastTree?.close()
    }
}
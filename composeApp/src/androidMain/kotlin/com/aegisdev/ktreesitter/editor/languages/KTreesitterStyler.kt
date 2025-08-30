package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.editor.language.base.styler.LanguageStyler
import com.aegisdev.ktreesitter.editor.model.SyntaxHighlightResult
import com.aegisdev.ktreesitter.editor.model.TextStructure
import com.aegisdev.ktreesitter.editor.model.TokenType
import io.github.treesitter.ktreesitter.Parser
import io.github.treesitter.ktreesitter.Query
import io.github.treesitter.ktreesitter.Tree
import io.github.treesitter.ktreesitter.Language as KtLanguage

class KTreesitterStyler(
    private val parser: Parser,
    queryString: String,
    ktLanguage: KtLanguage
) : LanguageStyler {

    private val query = Query(ktLanguage, queryString)
    private var lastTree: Tree? = null

    private val tokenTypeMap = mapOf(
        "variable" to TokenType.VARIABLE,
        "function.method" to TokenType.METHOD,
        "function.builtin" to TokenType.METHOD,
        "attribute" to TokenType.ATTR_NAME,
        "type" to TokenType.TYPE,
        "type.builtin" to TokenType.TYPE,
        "constructor" to TokenType.TYPE,
        "constant" to TokenType.LANG_CONST,
        "constant.builtin" to TokenType.LANG_CONST,
        "variable.builtin" to TokenType.VARIABLE,
        "number" to TokenType.NUMBER,
        "string" to TokenType.STRING,
        "string.escape" to TokenType.STRING,
        "escape" to TokenType.STRING,
        "comment" to TokenType.COMMENT,
        "keyword" to TokenType.KEYWORD,
        "operator" to TokenType.OPERATOR,
        "property" to TokenType.VARIABLE,
        "function" to TokenType.METHOD,
        "embedded" to TokenType.STRING,
        "punctuation.special" to TokenType.OPERATOR
    )

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val highlights = mutableListOf<SyntaxHighlightResult>()
        val source = structure.text.toString()
        val oldTree = lastTree
        val newTree = parser.parse(source, oldTree)
        lastTree = newTree
        oldTree?.close()

        val cursor = query.execute(newTree.rootNode)
        for (match in cursor) {
            for (capture in match.captures) {
                val tokenType = tokenTypeMap[capture.name]
                if (tokenType != null) {
                    highlights.add(
                        SyntaxHighlightResult(
                            tokenType,
                            capture.node.startByte.toInt(),
                            capture.node.endByte.toInt()
                        )
                    )
                }
            }
        }
        return highlights
    }

    override fun release() {
        parser.close()
        lastTree?.close()
        query.close()
    }
}
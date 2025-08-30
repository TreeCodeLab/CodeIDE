package com.aegisdev.ktreesitter.editor.languages

// CORRECTED IMPORTS
import com.aegisdev.ktreesitter.editor.language.base.exception.ParseException
import com.aegisdev.ktreesitter.editor.language.base.model.ParseResult
import com.aegisdev.ktreesitter.editor.language.base.model.TextStructure
import com.aegisdev.ktreesitter.editor.language.base.parser.LanguageParser
import io.github.treesitter.ktreesitter.Tree

class KTreesitterParser(
    private val languageManager: LanguageManager,
    private val languageName: String
) : LanguageParser {
    override fun execute(structure: TextStructure): ParseResult {
        val source = structure.text.toString()
        val parser = languageManager.borrowParser(languageName) ?: return ParseResult(null)
        var tree: Tree? = null
        var exception: ParseException? = null
        try {
            tree = parser.parse(source)
            val cursor = tree.walk()
            if (cursor.gotoFirstChild()) {
                var keepGoing = true
                while (keepGoing) {
                    if (cursor.currentNode.isError) {
                        val node = cursor.currentNode
                        exception = ParseException(
                            "Syntax Error",
                            node.startPoint.row.toInt() + 1,
                            node.startPoint.column.toInt()
                        )
                        break
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
            cursor.close()
        } finally {
            tree?.close()
            languageManager.returnParser(languageName, parser)
        }
        return ParseResult(exception)
    }
}
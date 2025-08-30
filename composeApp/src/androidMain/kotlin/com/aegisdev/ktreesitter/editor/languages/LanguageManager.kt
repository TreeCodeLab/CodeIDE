package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.editor.language.base.Language
import com.aegisdev.ktreesitter.editor.model.TokenType
import com.aegisdev.ktreesitter.java.TreeSitterJava
import com.aegisdev.ktreesitter.python.TreeSitterPython
import io.github.treesitter.ktreesitter.Language as KtLanguage

class LanguageManager {
    private val languageCache = mutableMapOf<String, Language>()

    init {
        val javaKtLanguage = KtLanguage(TreeSitterJava())
        val javaTypeMap = mapOf(
            "public" to TokenType.KEYWORD, "static" to TokenType.KEYWORD, "final" to TokenType.KEYWORD,
            "class" to TokenType.KEYWORD, "void" to TokenType.KEYWORD, "new" to TokenType.KEYWORD,
            "for" to TokenType.KEYWORD, "if" to TokenType.KEYWORD, "return" to TokenType.KEYWORD,
            "integral_type" to TokenType.TYPE, "floating_point_type" to TokenType.TYPE,
            "boolean_type" to TokenType.TYPE, "type_identifier" to TokenType.TYPE,
            "identifier" to TokenType.VARIABLE, "method_declaration" to TokenType.METHOD,
            "string_literal" to TokenType.STRING, "decimal_integer_literal" to TokenType.NUMBER,
            "line_comment" to TokenType.COMMENT, "block_comment" to TokenType.COMMENT,
            "=" to TokenType.OPERATOR, "+" to TokenType.OPERATOR, "-" to TokenType.OPERATOR
        )
        languageCache["java"] = KTreesitterLanguage("java", javaKtLanguage, javaTypeMap)

        val pythonKtLanguage = KtLanguage(TreeSitterPython())
        val pythonTypeMap = mapOf(
            "def" to TokenType.KEYWORD, "class" to TokenType.KEYWORD, "if" to TokenType.KEYWORD,
            "else" to TokenType.KEYWORD, "elif" to TokenType.KEYWORD, "for" to TokenType.KEYWORD,
            "in" to TokenType.KEYWORD, "return" to TokenType.KEYWORD, "print" to TokenType.METHOD,
            "identifier" to TokenType.VARIABLE, "string" to TokenType.STRING, "integer" to TokenType.NUMBER,
            "comment" to TokenType.COMMENT, "+" to TokenType.OPERATOR, "-" to TokenType.OPERATOR
        )
        languageCache["python"] = KTreesitterLanguage("python", pythonKtLanguage, pythonTypeMap)
    }

    fun getLanguage(name: String): Language? = languageCache[name]
}
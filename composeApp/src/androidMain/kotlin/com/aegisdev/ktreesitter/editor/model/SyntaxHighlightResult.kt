package com.aegisdev.ktreesitter.editor.model

data class SyntaxHighlightResult(
    val tokenType: TokenType,
    val start: Int,
    val end: Int
)

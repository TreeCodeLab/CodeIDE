package com.aegisdev.ktreesitter.editor.language.base.parser

import com.aegisdev.ktreesitter.editor.language.base.model.ParseResult
import com.aegisdev.ktreesitter.editor.model.TextStructure

fun interface LanguageParser {
    companion object {
        val NO_OP = LanguageParser { ParseResult(null) }
    }
    fun execute(structure: TextStructure): ParseResult
}
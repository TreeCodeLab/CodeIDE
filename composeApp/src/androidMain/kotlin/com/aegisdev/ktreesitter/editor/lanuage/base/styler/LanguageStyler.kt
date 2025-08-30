package com.aegisdev.ktreesitter.editor.language.base.styler

import com.aegisdev.ktreesitter.editor.model.SyntaxHighlightResult
import com.aegisdev.ktreesitter.editor.model.TextStructure

interface LanguageStyler {
    fun execute(structure: TextStructure): List<SyntaxHighlightResult>
    fun release()
}
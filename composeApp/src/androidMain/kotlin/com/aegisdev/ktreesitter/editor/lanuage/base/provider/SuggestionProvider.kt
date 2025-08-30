package com.aegisdev.ktreesitter.editor.language.base.provider

import com.aegisdev.ktreesitter.editor.model.TextStructure
import com.aegisdev.ktreesitter.editor.model.Suggestion

interface SuggestionProvider {
    companion object {
        val NO_OP = object : SuggestionProvider {
            override fun getAll(): Set<Suggestion> = emptySet()
            override fun processAllLines(structure: TextStructure) = Unit
            override fun processLine(lineNumber: Int, text: CharSequence) = Unit
            override fun deleteLine(lineNumber: Int) = Unit
            override fun clearLines() = Unit
        }
    }
    fun getAll(): Set<Suggestion>
    fun processAllLines(structure: TextStructure)
    fun processLine(lineNumber: Int, text: CharSequence)
    fun deleteLine(lineNumber: Int)
    fun clearLines()
}
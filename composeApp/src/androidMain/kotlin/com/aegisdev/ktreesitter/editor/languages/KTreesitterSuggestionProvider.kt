package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.language.base.model.Suggestion
import com.aegisdev.ktreesitter.language.base.model.TextStructure
import com.aegisdev.ktreesitter.language.base.provider.SuggestionProvider
import com.aegisdev.ktreesitter.language.base.utils.WordsManager

class KTreesitterSuggestionProvider : SuggestionProvider {

    private val wordsManager = WordsManager()

    override fun getAll(): Set<Suggestion> {
        return wordsManager.getWords()
    }

    override fun processAllLines(structure: TextStructure) {
        wordsManager.processAllLines(structure)
    }

    override fun processLine(lineNumber: Int, text: CharSequence) {
        wordsManager.processLine(lineNumber, text)
    }

    override fun deleteLine(lineNumber: Int) {
        wordsManager.deleteLine(lineNumber)
    }

    override fun clearLines() {
        wordsManager.clearLines()
    }
}
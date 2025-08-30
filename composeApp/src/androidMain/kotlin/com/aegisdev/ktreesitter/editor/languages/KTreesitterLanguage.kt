package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.editor.language.base.Language
import com.aegisdev.ktreesitter.editor.language.base.parser.LanguageParser
import com.aegisdev.ktreesitter.editor.language.base.provider.SuggestionProvider
import com.aegisdev.ktreesitter.editor.language.base.styler.LanguageStyler
import com.aegisdev.ktreesitter.editor.model.TokenType
import io.github.treesitter.ktreesitter.Parser
import io.github.treesitter.ktreesitter.Language as KtLanguage

class KTreesitterLanguage(
    override val languageName: String,
    private val ktLanguage: KtLanguage,
    private val typeMap: Map<String, TokenType>
) : Language {
    override fun getParser(): LanguageParser = LanguageParser.NO_OP
    override fun getProvider(): SuggestionProvider = SuggestionProvider.NO_OP
    override fun getStyler(): LanguageStyler = KTreesitterStyler(Parser(ktLanguage), typeMap)
}
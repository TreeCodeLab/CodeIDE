package com.aegisdev.ktreesitter.editor.widget.internal

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.util.AttributeSet
import com.aegisdev.ktreesitter.editor.model.*
import com.aegisdev.ktreesitter.editor.utils.*
import com.aegisdev.ktreesitter.editor.language.base.Language
import com.aegisdev.ktreesitter.editor.language.base.styler.LanguageStyler
import kotlinx.coroutines.*

abstract class SyntaxHighlightEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle,
) : UndoRedoEditText(context, attrs, defStyleAttr) {

    private var job: Job? = null
    private var styler: LanguageStyler? = null

    var language: Language? = null
        set(value) {
            field = value
            styler?.release()
            styler = value?.getStyler()
            onLanguageChanged()
        }

    var colorScheme: ColorScheme = EditorTheme.DARCULA
        set(value) {
            field = value
            onColorSchemeChanged()
        }

    var useSpacesInsteadOfTabs = true
    var tabWidth = 4

    private val findResults = mutableListOf<FindResult>()
    private var findResultStyleSpan: StyleSpan? = null
    private var addedTextCount = 0
    private var selectedFindResult = 0
    private var isSyntaxHighlighting = false
    private var isErrorSpansVisible = false

    init {
        filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source == "\t") tab() else null
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
        styler?.release()
    }

    override fun setTextContent(text: CharSequence) {
        styler?.release()
        styler = language?.getStyler()
        findResults.clear()
        super.setTextContent(text)
        syntaxHighlight()
    }

    override fun doBeforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        job?.cancel()
        addedTextCount -= count
        if (!isSyntaxHighlighting) {
            super.doBeforeTextChanged(text, start, count, after)
        }
        abortFling()
    }

    override fun doOnTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        addedTextCount += count
        if (!isSyntaxHighlighting) {
            super.doOnTextChanged(text, start, before, count)
        }
    }

    override fun doAfterTextChanged(text: Editable?) {
        if (!isSyntaxHighlighting) {
            shiftSpans(selectionStart, addedTextCount)
        }
        addedTextCount = 0
        syntaxHighlight()
    }

    protected open fun onLanguageChanged() {
        syntaxHighlight()
    }

    protected open fun onColorSchemeChanged() {
        findResultStyleSpan = StyleSpan(color = colorScheme.findResultBackgroundColor)
        setTextColor(colorScheme.textColor)
        setCursorDrawableColor(colorScheme.cursorColor)
        setBackgroundColor(colorScheme.backgroundColor)
        highlightColor = colorScheme.selectionColor
        syntaxHighlight()
    }

    private fun syntaxHighlight() {
        job?.cancel()
        val localStyler = styler ?: return
        val localStructure = structure

        job = CoroutineScope(Dispatchers.Default).launch {
            val results = localStyler.execute(localStructure)
            withContext(Dispatchers.Main) {
                updateSyntaxHighlighting(results)
            }
        }
    }

    private fun updateSyntaxHighlighting(results: List<SyntaxHighlightResult>) {
        if (layout == null) return
        isSyntaxHighlighting = true
        val lineStart = layout.getLineStart(topVisibleLine)
        val lineEnd = layout.getLineEnd(bottomVisibleLine)

        val spans = text.getSpans(lineStart, lineEnd, SyntaxHighlightSpan::class.java)
        for (span in spans) { text.removeSpan(span) }

        for (result in results) {
            val spanStart = maxOf(result.start, lineStart)
            val spanEnd = minOf(result.end, lineEnd)
            if (spanStart < spanEnd) {
                text.setSpan(
                    SyntaxHighlightSpan(StyleSpan(color = colorScheme.getColorFor(result.tokenType))),
                    spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        isSyntaxHighlighting = false
    }

    // ... All other methods from the original SyntaxHighlightEditText.kt (tab, find, etc.) go here ...
    // They do not need to be changed.
    fun tab(): String {
        return if (useSpacesInsteadOfTabs) " ".repeat(tabWidth) else "\t"
    }
    // ... (and so on for find, findNext, replace, etc.)
    private fun shiftSpans(from: Int, byHowMuch: Int) { /* ... unchanged ... */ }
    private fun ColorScheme.getColorFor(tokenType: TokenType): Int = when (tokenType) {
        TokenType.NUMBER -> this.numberColor
        TokenType.OPERATOR -> this.operatorColor
        TokenType.KEYWORD -> this.keywordColor
        TokenType.TYPE -> this.typeColor
        TokenType.LANG_CONST -> this.langConstColor
        TokenType.PREPROCESSOR -> this.preprocessorColor
        TokenType.VARIABLE -> this.variableColor
        TokenType.METHOD -> this.methodColor
        TokenType.STRING -> this.stringColor
        TokenType.COMMENT -> this.commentColor
        TokenType.TAG -> this.tagColor
        TokenType.TAG_NAME -> this.tagNameColor
        TokenType.ATTR_NAME -> this.attrNameColor
        TokenType.ATTR_VALUE -> this.attrValueColor
        TokenType.ENTITY_REF -> this.entityRefColor
    }
}
package com.aegisdev.ktreesitter.editor.plugin.base

import android.graphics.Canvas
import android.graphics.Typeface
import android.text.Editable
import android.view.KeyEvent
import android.view.MotionEvent
import com.aegisdev.ktreesitter.editor.model.ColorScheme
import com.aegisdev.ktreesitter.editor.model.UndoStack
import com.aegisdev.ktreesitter.editor.widget.TextProcessor
import com.aegisdev.ktreesitter.editor.language.base.Language
import com.aegisdev.ktreesitter.editor.model.TextStructure

abstract class EditorPlugin(val pluginId: String) {
    private var _editText: TextProcessor? = null
    protected val editText: TextProcessor get() = _editText!!
    protected val isAttached: Boolean get() = _editText != null
    protected val language: Language? get() = editText.language
    protected val colorScheme: ColorScheme get() = editText.colorScheme
    protected val structure: TextStructure get() = editText.structure
    protected val undoStack: UndoStack get() = editText.undoStack
    protected val redoStack: UndoStack get() = editText.redoStack
    open fun onAttached(editText: TextProcessor) {
        this._editText = editText
        onColorSchemeChanged(colorScheme)
        onLanguageChanged(language)
    }
    open fun onDetached(editText: TextProcessor) { this._editText = null }
    open fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = Unit
    open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
    open fun drawBehind(canvas: Canvas) = Unit
    open fun onDraw(canvas: Canvas) = Unit
    open fun onColorSchemeChanged(colorScheme: ColorScheme) = Unit
    open fun onLanguageChanged(language: Language?) = Unit
    open fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) = Unit
    open fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = Unit
    open fun onSelectionChanged(selStart: Int, selEnd: Int) = Unit
    open fun onTouchEvent(event: MotionEvent): Boolean = false
    open fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean = false
    open fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = false
    open fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit
    open fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = Unit
    open fun afterTextChanged(text: Editable?) = Unit
    open fun processLine(lineNumber: Int, lineStart: Int, lineEnd: Int) = Unit
    open fun addLine(lineNumber: Int, lineStart: Int) = Unit
    open fun removeLine(lineNumber: Int) = Unit
    open fun setTextContent(text: CharSequence) = Unit
    open fun setTextSize(size: Float) = Unit
    open fun setTypeface(tf: Typeface?) = Unit
    open fun showDropDown() = Unit
    protected fun requireContext() = _editText?.context ?: throw IllegalStateException("EditorPlugin $this not attached to a context.")
}
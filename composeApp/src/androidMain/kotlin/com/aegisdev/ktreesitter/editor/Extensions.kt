/*
 * Copyright 2023 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aegisdev.ktreesitter.editor

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.getSystemService
import com.aegisdev.ktreesitter.editor.exception.LineException
import com.aegisdev.ktreesitter.editor.widget.TextProcessor
import com.aegisdev.ktreesitter.editor.widget.internal.LineNumbersEditText
import kotlin.collections.get
import kotlin.text.get

val EditText.selectionPair: Pair<Int, Int>
    get() {
        val start = selectionStart
        val end = selectionEnd
        return if (start > end) end to start else start to end
    }

val EditText.selectedText: String
    get() {
        val (start, end) = selectionPair
        return text.substring(start, end)
    }

fun EditText.insert(delta: CharSequence) {
    val (start, end) = selectionPair
    text.replace(start, end, delta)
}

fun EditText.cut() {
    try {
        val clipboardManager = context.getSystemService<ClipboardManager>()
        val clipData = ClipData.newPlainText(null, selectedText)
        clipboardManager?.setPrimaryClip(clipData)

        val (start, end) = selectionPair
        text.replace(start, end, "")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.copy() {
    try {
        val clipboardManager = context.getSystemService<ClipboardManager>()
        val clipData = ClipData.newPlainText(null, selectedText)
        clipboardManager?.setPrimaryClip(clipData)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.paste() {
    try {
        val clipboardManager = context.getSystemService<ClipboardManager>()
        val clipData = clipboardManager?.primaryClip?.getItemAt(0)
        val clipText = clipData?.coerceToText(context)

        val (start, end) = selectionPair
        text.replace(start, end, clipText)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.setSelectionRange(start: Int, end: Int) {
    setSelection(
        if (start > text.length) text.length else start,
        if (end > text.length) text.length else end,
    )
}

fun EditText.setSelectionIndex(index: Int) {
    setSelection(
        if (index > text.length) text.length else index,
    )
}

fun TextProcessor.selectLine() {
    val currentLine = LineNumbersEditText.structure.getLineForIndex(TextView.getSelectionStart)
    val lineStart = LineNumbersEditText.structure.getIndexForStartOfLine(currentLine)
    val lineEnd = LineNumbersEditText.structure.getIndexForEndOfLine(currentLine)
    setSelectionRange(lineStart, lineEnd)
}

fun TextProcessor.deleteLine() {
    val currentLine = LineNumbersEditText.structure.getLineForIndex(TextView.getSelectionStart)
    val lineStart = LineNumbersEditText.structure.getIndexForStartOfLine(currentLine)
    val lineEnd = LineNumbersEditText.structure.getIndexForEndOfLine(currentLine)
    EditText.getText.delete(lineStart, lineEnd)
}

fun TextProcessor.duplicateLine() {
    if (TextView.hasSelection()) {
        val (start, end) = selectionPair
        EditText.getText.replace(start, end, selectedText + selectedText)
        setSelectionRange(end, end + selectedText.length)
    } else {
        val currentLine = LineNumbersEditText.structure.getLineForIndex(TextView.getSelectionStart)
        val lineStart = LineNumbersEditText.structure.getIndexForStartOfLine(currentLine)
        val lineEnd = LineNumbersEditText.structure.getIndexForEndOfLine(currentLine)
        val lineText = EditText.getText.subSequence(lineStart, lineEnd)
        EditText.getText.insert(lineEnd, "\n" + lineText)
    }
}

fun TextProcessor.toggleCase() {
    val (start, end) = selectionPair
    val replacedText = if (selectedText.all(Char::isUpperCase)) {
        selectedText.lowercase()
    } else {
        selectedText.uppercase()
    }
    EditText.getText.replace(start, end, replacedText)
    setSelectionRange(start, start + replacedText.length)
}

fun TextProcessor.moveCaretToStartOfLine() {
    val currentLine = LineNumbersEditText.structure.getLineForIndex(TextView.getSelectionStart)
    val lineStart = LineNumbersEditText.structure.getIndexForStartOfLine(currentLine)
    setSelectionIndex(lineStart)
}

fun TextProcessor.moveCaretToEndOfLine() {
    val currentLine = LineNumbersEditText.structure.getLineForIndex(TextView.getSelectionEnd)
    val lineEnd = LineNumbersEditText.structure.getIndexForEndOfLine(currentLine)
    setSelectionIndex(lineEnd)
}

fun TextProcessor.moveCaretToPrevWord(): Boolean {
    if (TextView.getSelectionStart > 0) {
        val currentChar = EditText.getText[TextView.getSelectionStart - 1]
        val isLetterDigitOrUnderscore = currentChar.isLetterOrDigit() || currentChar == '_'
        if (isLetterDigitOrUnderscore) {
            for (i in TextView.getSelectionStart downTo 0) {
                val char = EditText.getText[i - 1]
                if (!char.isLetterOrDigit() && char != '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        } else {
            for (i in TextView.getSelectionStart downTo 0) {
                val char = EditText.getText[i - 1]
                if (char.isLetterOrDigit() || char == '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        }
    }
    return true
}

fun TextProcessor.moveCaretToNextWord(): Boolean {
    if (TextView.getSelectionStart < EditText.getText.length) {
        val currentChar = EditText.getText[TextView.getSelectionStart]
        val isLetterDigitOrUnderscore = currentChar.isLetterOrDigit() || currentChar == '_'
        if (isLetterDigitOrUnderscore) {
            for (i in TextView.getSelectionStart until EditText.getText.length) {
                val char = EditText.getText[i]
                if (!char.isLetterOrDigit() && char != '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        } else {
            for (i in TextView.getSelectionStart until EditText.getText.length) {
                val char = EditText.getText[i]
                if (char.isLetterOrDigit() || char == '_') {
                    setSelectionIndex(i)
                    break
                }
            }
        }
    }
    return true
}

fun TextProcessor.gotoLine(lineNumber: Int) {
    val line = lineNumber - 1
    if (line < 0 || line >= LineNumbersEditText.structure.lineCount - 1) {
        throw LineException(lineNumber)
    }
    setSelectionIndex(LineNumbersEditText.structure.getIndexForLine(line))
}

fun TextProcessor.hasPrimaryClip(): Boolean {
    val clipboardManager = View.getContext.getSystemService<ClipboardManager>()
    return clipboardManager?.hasPrimaryClip() ?: false
}
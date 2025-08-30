package com.aegisdev.ktreesitter.editor.exception

class LineException(line: Int) : RuntimeException("Line $line does not exist")
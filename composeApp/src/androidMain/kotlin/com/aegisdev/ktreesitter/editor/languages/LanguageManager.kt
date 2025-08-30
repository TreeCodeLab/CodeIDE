package com.aegisdev.ktreesitter.editor.languages

import com.aegisdev.ktreesitter.editor.language.base.Language
import com.aegisdev.ktreesitter.java.TreeSitterJava
import com.aegisdev.ktreesitter.python.TreeSitterPython
import io.github.treesitter.ktreesitter.Language as KtLanguage

class LanguageManager {
    private val languageCache = mutableMapOf<String, Language>()

    // TODO: Load languages and queries from assets or a configuration file.
    init {
        val javaKtLanguage = KtLanguage(TreeSitterJava())
        val javaQuery = """
            ; Variables

            (identifier) @variable

            ; Methods

            (method_declaration
              name: (identifier) @function.method)
            (method_invocation
              name: (identifier) @function.method)
            (super) @function.builtin

            ; Annotations

            (annotation
              name: (identifier) @attribute)
            (marker_annotation
              name: (identifier) @attribute)

            "@" @operator

            ; Types

            (type_identifier) @type

            (interface_declaration
              name: (identifier) @type)
            (class_declaration
              name: (identifier) @type)
            (enum_declaration
              name: (identifier) @type)

            ((field_access
              object: (identifier) @type)
             (#match? @type "^[A-Z]"))
            ((scoped_identifier
              scope: (identifier) @type)
             (#match? @type "^[A-Z]"))
            ((method_invocation
              object: (identifier) @type)
             (#match? @type "^[A-Z]"))
            ((method_reference
              . (identifier) @type)
             (#match? @type "^[A-Z]"))

            (constructor_declaration
              name: (identifier) @type)

            [
              (boolean_type)
              (integral_type)
              (floating_point_type)
              (floating_point_type)
              (void_type)
            ] @type.builtin

            ; Constants

            ((identifier) @constant
             (#match? @constant "^_*[A-Z][A-Z\\d_]+$"))

            ; Builtins

            (this) @variable.builtin

            ; Literals

            [
              (hex_integer_literal)
              (decimal_integer_literal)
              (octal_integer_literal)
              (decimal_floating_point_literal)
              (hex_floating_point_literal)
            ] @number

            [
              (character_literal)
              (string_literal)
            ] @string
            (escape_sequence) @string.escape

            [
              (true)
              (false)
              (null_literal)
            ] @constant.builtin

            [
              (line_comment)
              (block_comment)
            ] @comment

            ; Keywords

            [
              "abstract"
              "assert"
              "break"
              "case"
              "catch"
              "class"
              "continue"
              "default"
              "do"
              "else"
              "enum"
              "exports"
              "extends"
              "final"
              "finally"
              "for"
              "if"
              "implements"
              "import"
              "instanceof"
              "interface"
              "module"
              "native"
              "new"
              "non-sealed"
              "open"
              "opens"
              "package"
              "permits"
              "private"
              "protected"
              "provides"
              "public"
              "requires"
              "record"
              "return"
              "sealed"
              "static"
              "strictfp"
              "switch"
              "synchronized"
              "throw"
              "throws"
              "to"
              "transient"
              "transitive"
              "try"
              "uses"
              "volatile"
              "when"
              "while"
              "with"
              "yield"
            ] @keyword
        """.trimIndent()
        languageCache["java"] = KTreesitterLanguage("java", javaKtLanguage, javaQuery)

        val pythonKtLanguage = KtLanguage(TreeSitterPython())
        val pythonQuery = """
            ; Identifier naming conventions

            (identifier) @variable

            ((identifier) @constructor
             (#match? @constructor "^[A-Z]"))

            ((identifier) @constant
             (#match? @constant "^[A-Z][A-Z_]*$"))

            ; Function calls

            (decorator) @function
            (decorator
              (identifier) @function)

            (call
              function: (attribute attribute: (identifier) @function.method))
            (call
              function: (identifier) @function)

            ; Builtin functions

            ((call
              function: (identifier) @function.builtin)
             (#match?
               @function.builtin
               "^(abs|all|any|ascii|bin|bool|breakpoint|bytearray|bytes|callable|chr|classme
            thod|compile|complex|delattr|dict|dir|divmod|enumerate|eval|exec|filter|float|fo
            rmat|frozenset|getattr|globals|hasattr|hash|help|hex|id|input|int|isinstance|iss
            ubclass|iter|len|list|locals|map|max|memoryview|min|next|object|oct|open|ord|pow
            |print|property|range|repr|reversed|round|set|setattr|slice|sorted|staticmethod|
            str|sum|super|tuple|type|vars|zip|__import__)$"))

            ; Function definitions

            (function_definition
              name: (identifier) @function)

            (attribute attribute: (identifier) @property)
            (type (identifier) @type)

            ; Literals

            [
              (none)
              (true)
              (false)
            ] @constant.builtin

            [
              (integer)
              (float)
            ] @number

            (comment) @comment
            (string) @string
            (escape_sequence) @escape

            (interpolation
              "{" @punctuation.special
              "}" @punctuation.special) @embedded

            [
              "-"
              "-="
              "!="
              "*"
              "**"
              "**="
              "*="
              "/"
              "//"
              "//="
              "/="
              "&"
              "&="
              "%"
              "%="
              "^"
              "^="
              "+"
              "->"
              "+="
              "<"
              "<<"
              "<<="
              "<="
              "<>"
              "="
              ":="
              "=="
              ">"
              ">="
              ">>"
              ">>="
              "|"
              "|="
              "~"
              "@="
              "and"
              "in"
              "is"
              "not"
              "or"
              "is not"
              "not in"
            ] @operator

            [
              "as"
              "assert"
              "async"
              "await"
              "break"
              "class"
              "continue"
              "def"
              "del"
              "elif"
              "else"
              "except"
              "exec"
              "finally"
              "for"
              "from"
              "global"
              "if"
              "import"
              "lambda"
              "nonlocal"
              "pass"
              "print"
              "raise"
              "return"
              "try"
              "while"
              "with"
              "yield"
              "match"
              "case"
            ] @keyword
        """.trimIndent()
        languageCache["python"] = KTreesitterLanguage("python", pythonKtLanguage, pythonQuery)
    }

    fun getLanguage(name: String): Language? = languageCache[name]
}
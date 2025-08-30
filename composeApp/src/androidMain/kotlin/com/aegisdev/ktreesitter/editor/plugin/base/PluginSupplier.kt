package com.aegisdev.ktreesitter.editor.plugin.base

class PluginSupplier private constructor() {
    private val plugins = mutableSetOf<EditorPlugin>()
    fun <T : EditorPlugin> plugin(plugin: T) { plugins.add(plugin) }
    fun supply(): Set<EditorPlugin> = plugins
    companion object {
        fun create(block: PluginSupplier.() -> Unit): PluginSupplier = PluginSupplier().apply(block)
    }
}
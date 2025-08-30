package com.aegisdev.ktreesitter.editor.plugin.base

interface PluginContainer {
    fun plugins(supplier: PluginSupplier)
    fun <T : EditorPlugin> installPlugin(plugin: T)
    fun uninstallPlugin(pluginId: String)
    fun <T : EditorPlugin> findPlugin(pluginId: String): T?
    fun hasPlugin(pluginId: String): Boolean
}
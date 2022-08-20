package net.accelf.contral.api.plugin

import androidx.annotation.RestrictTo
import androidx.compose.runtime.staticCompositionLocalOf

@RestrictTo(RestrictTo.Scope.LIBRARY)
data class Plugin(
    val id: String,
    val name: String,
    val version: Version,
    val dependencies: Map<String, MinorVersion>,
)

@RestrictTo(RestrictTo.Scope.LIBRARY)
val LocalPlugins = staticCompositionLocalOf<List<Plugin>> { error("LocalPlugins is not set") }

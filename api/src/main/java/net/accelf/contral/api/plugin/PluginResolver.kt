package net.accelf.contral.api.plugin

import androidx.annotation.RestrictTo

class PluginResolver(
    private val id: String,
) {
    var name: String? = null
    var version: Version? = null

    private val dependencies = mutableMapOf<String, MinorVersion>()
    fun require(name: String, version: MinorVersion) {
        dependencies[name]?.let { old ->
            if (old.major != version.major) {
                error("Two duplicated dependencies for $id requires different major version: $old and $version")
            }

            if (old.minor >= version.minor) {
                return
            }
        }
        dependencies[name] = version
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun build() = Plugin(
        id = id,
        name = name ?: id,
        version = version ?: error("Plugin version for $id not defined"),
        dependencies = dependencies,
    )
}

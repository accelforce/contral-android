package net.accelf.contral.core.plugin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.VisibleForTesting
import dalvik.system.DexClassLoader
import net.accelf.contral.api.plugin.Plugin
import net.accelf.contral.api.plugin.PluginResolver
import net.accelf.contral.core.corePlugin
import okio.FileSystem.Companion.SYSTEM
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip

fun resolvePlugins(context: Context): List<Plugin> =
    mutableListOf(PluginResolver("core").apply(PluginResolver::corePlugin).build())
        .apply { addAll(collectPluginsFromPackages(context)) }
        .also { plugins ->
            val map = plugins.associateBy(Plugin::id)
            plugins.forEach { child ->
                child.dependencies.forEach { (parentId, requireVersion) ->
                    map[parentId]?.let { parent ->
                        if (parent.version.major == requireVersion.major) {
                            return@let
                        }

                        if (parent.version.minor >= requireVersion.minor) {
                            return@let
                        }

                        @Suppress("MaxLineLength")
                        error("Plugin ${child.name} requires version $requireVersion of plugin ${parent.name} but installed version is ${parent.version}")
                    }
                        ?: error("Plugin $parentId required by ${child.name} is not installed.")
                }
            }
        }

private fun collectPluginsFromPackages(context: Context) =
    context.packageManager.getInstalledApplicationsCompat(PackageManager.GET_META_DATA)
        .filter { it.metaData?.getBoolean("contralPlugin", false) ?: false }
        .map { it.publicSourceDir }
        .let { paths ->
            val loader = DexClassLoader(
                paths.joinToString(":"),
                null,
                null,
                PluginResolver::class.java.classLoader,
            )
            paths.map {
                SYSTEM.openZip(it.toPath())
                    .source("contral".toPath())
                    .buffer()
                    .readUtf8()
                    .lines()
            }
                .flatten()
                .filter(String::isNotBlank)
                .mapNotNull {
                    val index = it.lastIndexOf('.')
                    runCatching {
                        PluginResolver(it)
                            .apply {
                                loader.loadClass(it.substring(0, index))
                                    .getMethod(it.substring(index + 1), PluginResolver::class.java)
                                    .invoke(null, this)
                            }
                            .build()
                    }.getOrNull()
                }
        }

@Suppress("DEPRECATION")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun PackageManager.getInstalledApplicationsCompat(flags: Int): List<ApplicationInfo> = when {
    Build.VERSION.SDK_INT < 33 -> getInstalledApplications(flags)
    else -> getInstalledApplications(PackageManager.ApplicationInfoFlags.of(flags.toLong()))
}

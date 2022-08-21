package net.accelf.contral.api.plugin

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.navigation.NavGraphBuilder

@RestrictTo(RestrictTo.Scope.LIBRARY)
class Plugin(
    val id: String,
    val name: String,
    val version: Version,
    val dependencies: Map<String, MinorVersion>,
    val injects: List<@Composable () -> ProvidedValue<*>>,
    val renderRoutes: (NavGraphBuilder).() -> Unit,
)

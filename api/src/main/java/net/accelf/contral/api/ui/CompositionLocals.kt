package net.accelf.contral.api.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import net.accelf.contral.api.timelines.Timeline

val LocalNavController = staticCompositionLocalOf<NavController> { error("LocalNavController is not set") }
val LocalRegisterTimeline = staticCompositionLocalOf<suspend (Timeline) -> Long> {
    error("LocalRegisterTimeline is not set")
}

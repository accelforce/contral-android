package net.accelf.contral.api.ui

import androidx.navigation.NavController
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.ui.utils.staticCompositionLocalOf

val LocalNavController by staticCompositionLocalOf<NavController>()
val LocalRegisterTimeline by staticCompositionLocalOf<suspend (Timeline) -> Long>()

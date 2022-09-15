package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import net.accelf.contral.api.ui.utils.compositionLocalOf

interface TimelineItem {
    @Composable
    fun Render()
}

val LocalTimelineItem by compositionLocalOf<TimelineItem>()

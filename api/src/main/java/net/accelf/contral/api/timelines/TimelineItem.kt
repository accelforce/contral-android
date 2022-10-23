package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import net.accelf.contral.api.ui.utils.compositionLocalOf

interface TimelineItem {
    @Composable
    fun Render()

    @Composable
    @Suppress("ComposableNaming")
    fun getOnSelected(setOnSelected: (() -> Unit) -> Unit)
}

val LocalTimelineItem by compositionLocalOf<TimelineItem>()

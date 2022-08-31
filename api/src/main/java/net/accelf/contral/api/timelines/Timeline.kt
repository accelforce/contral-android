package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.paging.Pager

interface Timeline {
    fun pager(): Pager<*, out TimelineItem>

    @Composable
    fun Render()
}

val LocalTimeline = compositionLocalOf<Timeline> { error("LocalTimeline is not set") }

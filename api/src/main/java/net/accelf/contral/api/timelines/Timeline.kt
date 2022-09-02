package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.paging.Pager
import net.accelf.contral.api.composers.Composer

interface Timeline {
    fun pager(): Pager<*, out TimelineItem>

    fun composer(): Composer

    @Composable
    fun Render()
}

val LocalTimeline = compositionLocalOf<Timeline> { error("LocalTimeline is not set") }

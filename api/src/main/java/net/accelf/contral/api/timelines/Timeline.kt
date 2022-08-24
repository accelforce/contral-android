package net.accelf.contral.api.timelines

import androidx.compose.runtime.Composable
import androidx.paging.Pager

interface Timeline {
    fun pager(): Pager<*, out TimelineItem>

    @Composable
    fun Render()
}

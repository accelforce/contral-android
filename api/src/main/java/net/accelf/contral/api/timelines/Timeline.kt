package net.accelf.contral.api.timelines

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.paging.Pager
import net.accelf.contral.api.composers.Composer

interface Timeline {
    @Composable
    @SuppressLint("ComposableNaming")
    fun getPager(setPager: (Pager<*, out TimelineItem>) -> Unit)

    @Composable
    @SuppressLint("ComposableNaming")
    fun getComposer(setComposer: (Composer) -> Unit)

    @Composable
    fun Render()
}

val LocalTimeline = compositionLocalOf<Timeline> { error("LocalTimeline is not set") }

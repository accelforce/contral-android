package net.accelf.contral.api.timelines

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.paging.Pager
import net.accelf.contral.api.composers.Composer
import net.accelf.contral.api.ui.utils.compositionLocalOf

abstract class AbstractTimeline : Timeline {
    @Composable
    override fun ProvideValues(content: @Composable () -> Unit) {
        content()
    }

    @Composable
    @SuppressLint("ComposableNaming")
    override fun getComposer(setComposer: (Composer) -> Unit) {}
}

interface Timeline {
    @Composable
    fun ProvideValues(content: @Composable () -> Unit)

    @Composable
    @SuppressLint("ComposableNaming")
    fun getPager(setPager: (Pager<*, out TimelineItem>) -> Unit)

    @Composable
    fun Render()

    @Composable
    @SuppressLint("ComposableNaming")
    fun getComposer(setComposer: (Composer) -> Unit)
}

val LocalTimeline by compositionLocalOf<Timeline>()

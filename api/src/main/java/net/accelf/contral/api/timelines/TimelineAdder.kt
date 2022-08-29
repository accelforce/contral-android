package net.accelf.contral.api.timelines

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable

@RestrictTo(RestrictTo.Scope.LIBRARY)
class TimelineAdder(
    val render: @Composable () -> Unit,
    val onClick: ((String) -> Unit) -> Unit,
)

package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.core.LocalTimelineAdders
import net.accelf.contral.core.LocalTimelines

@Composable
internal fun ListTimelinesPage() {
    val timelines = LocalTimelines.current
    val timelineAdders = LocalTimelineAdders.current
    val navController = LocalNavController.current

    ListTimelines(
        timelines = timelines,
        timelineAdders = timelineAdders,
        navigate = navController::navigate,
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ListTimelines(
    timelines: List<Timeline>,
    timelineAdders: List<TimelineAdder>,
    navigate: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            timelineAdders.forEach { timelineAdder ->
                DropdownMenuItem(
                    text = timelineAdder.render,
                    onClick = {
                        scope.launch {
                            bottomSheetState.hide()
                            timelineAdder.onClick(navigate)
                        }
                    },
                )
            }
        },
        sheetState = bottomSheetState,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
            ) {
                timelines.forEachIndexed { i, timeline ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigate("timelines/$i")
                            },
                    ) {
                        timeline.Render()
                    }
                }
            }

            AddTimelineButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            ) {
                scope.launch {
                    bottomSheetState.show()
                }
            }
        }
    }
}

internal class PreviewTimelineAdderProvider : PreviewParameterProvider<TimelineAdder> {
    override val values: Sequence<TimelineAdder>
        get() = sequenceOf(
            TimelineAdder(
                render = {
                    Text(text = "Add example timeline")
                },
                onClick = {},
            ),
            TimelineAdder(
                render = {
                    Text(text = "Add example public timeline")
                },
                onClick = {},
            ),
        )
}

@Composable
@Preview(heightDp = 320)
private fun PreviewListTimelines() {
    val timelines = List(30) {
        object : Timeline {
            override fun pager() = throw NotImplementedError()

            @Composable
            override fun Render() {
                Text(
                    text = "Timeline $it",
                )
            }
        }
    }
    val timelineAdders = PreviewTimelineAdderProvider().values.toList()

    ContralTheme {
        ListTimelines(
            timelines = timelines,
            timelineAdders = timelineAdders,
            navigate = {},
        )
    }
}

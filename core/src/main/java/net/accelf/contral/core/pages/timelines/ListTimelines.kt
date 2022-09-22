@file:Suppress("ForbiddenImport")

package net.accelf.contral.core.pages.timelines

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import kotlinx.coroutines.launch
import net.accelf.contral.api.timelines.AbstractTimeline
import net.accelf.contral.api.timelines.Timeline
import net.accelf.contral.api.timelines.TimelineAdder
import net.accelf.contral.api.timelines.TimelineItem
import net.accelf.contral.api.ui.LocalNavController
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import net.accelf.contral.core.LocalContralDatabase
import net.accelf.contral.core.LocalTimelineAdders
import net.accelf.contral.core.LocalTimelineController

@Composable
internal fun ListTimelinesPage() {
    val db = LocalContralDatabase.current
    val timelineController = LocalTimelineController.current
    val savedTimelines by db.savedTimelineDao().listSavedTimelines().collectAsState(emptyList())
    val timelineAdders = LocalTimelineAdders.current
    val navController = LocalNavController.current

    ListTimelines(
        timelines = savedTimelines.associate { it.id to timelineController.getTimeline(it.params) },
        timelineAdders = timelineAdders,
        removeTimeline = db.savedTimelineDao()::delete,
        navigate = navController::navigate,
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun ListTimelines(
    timelines: Map<Long, Timeline>,
    timelineAdders: List<TimelineAdder>,
    removeTimeline: suspend (Long) -> Unit,
    navigate: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var showAddTimeline by useState(false)
    var actionTimelineId by useState<Long?>(null)
    val bottomSheetState = rememberModalBottomSheetState(
        if (showAddTimeline || actionTimelineId != null) {
            ModalBottomSheetValue.Expanded
        } else {
            ModalBottomSheetValue.Hidden
        },
        confirmStateChange = {
            showAddTimeline = false
            actionTimelineId = null
            false
        },
    )
    val actionTimeline = remember(timelines, actionTimelineId) { timelines[actionTimelineId] }

    ModalBottomSheetLayout(
        sheetContent = {
            if (actionTimelineId != null) {
                actionTimeline!!.Render()

                DropdownMenuItem(
                    text = { Text(text = "Delete this timeline") },
                    onClick = {
                        scope.launch {
                            val id = actionTimelineId!!
                            actionTimelineId = null
                            removeTimeline(id)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.error,
                    ),
                )
            } else if (timelineAdders.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(text = "No timeline adders found") },
                    onClick = {},
                )
            } else {
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
                timelines.forEach { (i, timeline) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    navigate("timelines/$i")
                                },
                                onLongClick = {
                                    actionTimelineId = i
                                },
                            ),
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
                    showAddTimeline = true
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
        it.toLong() to object : AbstractTimeline() {
            @Composable
            @SuppressLint("ComposableNaming")
            override fun getPager(setPager: (Pager<*, out TimelineItem>) -> Unit) {}

            @Composable
            override fun Render() {
                Text(
                    text = "Timeline $it",
                )
            }
        }
    }.toMap()
    val timelineAdders = PreviewTimelineAdderProvider().values.toList()

    ContralTheme {
        ListTimelines(
            timelines = timelines,
            timelineAdders = timelineAdders,
            removeTimeline = {},
            navigate = {},
        )
    }
}

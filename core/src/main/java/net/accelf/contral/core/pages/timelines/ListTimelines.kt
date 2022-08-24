package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.accelf.contral.core.LocalNavController
import net.accelf.contral.core.LocalTimelines

@Composable
internal fun ListTimelines() {
    val navController = LocalNavController.current
    val timelines = LocalTimelines.current

    Column {
        timelines.forEachIndexed { i, timeline ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("timelines/$i")
                    },
            ) {
                timeline.Render()
            }
        }
    }
}

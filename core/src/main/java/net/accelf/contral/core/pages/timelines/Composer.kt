package net.accelf.contral.core.pages.timelines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.accelf.contral.api.composers.Composer
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState
import kotlin.math.max

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun Composer(
    setMinHeight: (Dp) -> Unit,
    composer: Composer,
) {
    val scope = rememberCoroutineScope()
    var text by useState("")
    var sending by useState(false)

    val textStyle = LocalTextStyle.current
    val density = LocalDensity.current
    LaunchedEffect(text) {
        val lineCount = max(1, text.lines().size)
        val minHeight = TextFieldDefaults.MinHeight
        val heightPerLine = with(density) { textStyle.lineHeight.toDp() }
        setMinHeight(minHeight + heightPerLine * (lineCount - 1) + 12.dp)
    }

    Column {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 48.dp, height = 12.dp)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outline),
        )

        Row {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp),
                enabled = !sending,
            )

            Button(
                onClick = {
                    scope.launch {
                        sending = true
                        if (composer.compose(text)) {
                            text = ""
                        }
                        sending = false
                    }
                },
                modifier = Modifier
                    .requiredWidth(IntrinsicSize.Min)
                    .padding(bottom = 2.dp, end = 2.dp)
                    .align(Alignment.Bottom),
                enabled = !sending,
            ) {
                Text(text = "Post")
            }
        }
    }
}

internal object DummyComposer : Composer {
    override suspend fun compose(content: String): Boolean = false
}

@Composable
@Preview(widthDp = 300, heightDp = 300)
private fun PreviewComposer() {
    ContralTheme {
        Composer(
            setMinHeight = {},
            composer = DummyComposer,
        )
    }
}

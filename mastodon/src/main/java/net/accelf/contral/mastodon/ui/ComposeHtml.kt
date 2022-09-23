package net.accelf.contral.mastodon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import net.accelf.contral.api.ui.utils.useState
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

typealias Html = String

@Composable
internal fun HtmlText(
    html: Html,
    modifier: Modifier = Modifier,
    setClickable: (AnnotatedString.Range<String>) -> (() -> Unit)?,
) {
    val density = LocalDensity.current
    val annotatedString = html.htmlToAnnotatedString()
    var boxes by useState(emptyMap<Rect, () -> Unit>())
    val onTextLayout = remember(annotatedString, setClickable) {
        { layoutResult: TextLayoutResult ->
            boxes = annotatedString.getStringAnnotations(0, annotatedString.length)
                .mapNotNull { range ->
                    setClickable(range)?.let { onClick ->
                        (range.start until range.end).map { offset ->
                            layoutResult.getBoundingBox(offset) to onClick
                        }
                    }
                }
                .flatten()
                .toMap()
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        onTextLayout = onTextLayout,
    )

    boxes.forEach { (rect, onClick) ->
        Box(
            modifier = density.run {
                Modifier.offset(rect.left.toDp(), rect.top.toDp())
                    .size(rect.width.toDp(), rect.height.toDp())
                    .clickable { onClick() }
            },
        )
    }
}

@Composable
private fun Html.htmlToAnnotatedString(): AnnotatedString {
    val html = Jsoup.parse(this)
    return AnnotatedString.Builder()
        .apply(html.body())
        .toAnnotatedString()
}

enum class HtmlAnnotations {
    URL,
    ;

    val tag = "${this::class.qualifiedName}::$name"
}

@Composable
private fun AnnotatedString.Builder.apply(node: Node): AnnotatedString.Builder =
    apply {
        when (node) {
            is TextNode -> append(node.text())
            is Element -> when (node.tagName()) {
                "a" -> {
                    pushStringAnnotation(HtmlAnnotations.URL.tag, node.attr("href"))
                    pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
                    node.childNodes().forEach { apply(it) }
                    pop()
                    pop()
                }
                "br" -> append('\n')
                else -> {
                    node.childNodes().forEachIndexed { i, n ->
                        apply(n)
                        if (n is Element && n.tagName() == "p" && i < node.childNodeSize() - 1) {
                            append("\n\n")
                        }
                    }
                }
            }
        }
    }

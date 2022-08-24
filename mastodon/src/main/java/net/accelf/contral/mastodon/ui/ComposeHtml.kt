package net.accelf.contral.mastodon.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
    onClick: (List<AnnotatedString.Range<String>>) -> Unit,
) {
    val annotatedString = html.htmlToAnnotatedString()
    var layoutResult by useState<TextLayoutResult?>(null)
    val pressIndicator = Modifier.pointerInput("HtmlTextIndicator") {
        detectTapGestures { pos ->
            layoutResult?.let { layoutResult ->
                val offset = layoutResult.getOffsetForPosition(pos)
                onClick(annotatedString.getStringAnnotations(offset, offset))
            }
        }
    }

    BasicText(
        text = annotatedString,
        modifier = modifier.then(pressIndicator),
        onTextLayout = { layoutResult = it },
    )
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

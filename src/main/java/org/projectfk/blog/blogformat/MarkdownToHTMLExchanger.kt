package org.projectfk.blog.blogformat

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownToHTMLExchanger : ContentToHTMLExchanger{
    val parser = Parser.builder().build()!!
    val htmlRenderer = HtmlRenderer.builder().build()!!

    override fun exchangeToHTML(raw: String): String {
        return "<div>${htmlRenderer.render(parser.parse(raw))}</div>"
    }
}
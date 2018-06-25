package org.projectfk.blog.blogformat.converters

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer
import org.projectfk.blog.blogformat.ContentToHTMLExchanger

object MarkdownToHTMLExchanger : ContentToHTMLExchanger {

    private val parser = Parser.builder().build()!!
    private val htmlRenderer = HtmlRenderer.builder().build()!!

    override fun toHtml(raw: String): String = "<div>${htmlRenderer.render(parser.parse(raw))}</div>"

}
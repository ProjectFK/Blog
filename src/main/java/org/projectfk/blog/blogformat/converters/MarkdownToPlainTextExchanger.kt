package org.projectfk.blog.blogformat.converters

import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer
import org.projectfk.blog.blogformat.ContentToPlainTextExchanger

object MarkdownToPlainTextExchanger : ContentToPlainTextExchanger{

    private val renderer = TextContentRenderer.builder().build()!!
    private val parser = Parser.builder().build()!!

    override fun toPlainText(raw: String): String = renderer.render(parser.parse(raw))

}
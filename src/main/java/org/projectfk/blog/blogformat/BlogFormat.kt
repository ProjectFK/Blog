package org.projectfk.blog.blogformat

import org.projectfk.blog.blogformat.converters.MarkdownToHTMLExchanger
import org.projectfk.blog.blogformat.converters.MarkdownToPlainTextExchanger
import org.projectfk.blog.blogformat.converters.PlainTextToHtmlExchanger

enum class BlogFormat(
        val formatName: String,
        val htmlExchanger: ContentToHTMLExchanger,
        val plainTextExchanger: ContentToPlainTextExchanger
) {

    MARKDOWN("markdown", MarkdownToHTMLExchanger, object : ContentToPlainTextExchanger {
        override fun toPlainText(raw: String): String = raw
    }),
    PLAIN_TEXT("text", PlainTextToHtmlExchanger, MarkdownToPlainTextExchanger)

}
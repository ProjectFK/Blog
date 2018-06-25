package org.projectfk.blog.blogformat.converters

import org.projectfk.blog.blogformat.ContentToHTMLExchanger

object PlainTextToHtmlExchanger : ContentToHTMLExchanger {

    override fun toHtml(raw: String): String = "<div>${raw.replace("\n", "<br>")}<div>"

}
package org.projectfk.blog.blogformat

class PlainTextToHtmlExchanger : ContentToHTMLExchanger {
    override fun exchangeToHTML(raw: String): String = "<div>${raw.replace("\n", "<br>")}<div>"
}
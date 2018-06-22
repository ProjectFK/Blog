package org.projectfk.blog.blogformat

interface ContentToHTMLExchanger {

    fun exchangeToHTML(raw: String): String

}
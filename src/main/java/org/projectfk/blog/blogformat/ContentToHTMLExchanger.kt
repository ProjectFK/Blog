package org.projectfk.blog.blogformat

interface ContentToHTMLExchanger{

    fun toHtml(raw: String): String

}
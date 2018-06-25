package org.projectfk.blog.blogformat

interface ContentToPlainTextExchanger {
    fun toPlainText(raw: String): String
}
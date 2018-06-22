package org.projectfk.blog.blogformat

enum class BlogFormat(val formatName: String, val contentExchanger: ContentToHTMLExchanger) {

    MARKDOWN("markdown", MarkdownToHTMLExchanger()), PLAIN_TEXT("text", PlainTextToHtmlExchanger());

}
package org.projectfk.blog

import java.time.LocalDateTime
import java.util.*

interface Post {
    var postName: String
    val author: User
    var content: String
    val createDate: LocalDateTime
    val modifyDate: LocalDateTime
    val comments: List<Comment>
}

interface User {
    var name: String
}

interface Comment {
    val author: User
    var content: String
    val createDate: LocalDateTime
    val modifyDate: LocalDateTime
}

abstract class AbstractPost(postName: String, override val author: User, content: String) : Post {

    override var postName: String = postName
        set(value) {
            field = value
            refreshModifyDate()
        }

    override var content: String = content
        set(value) {
            field = value
            refreshModifyDate()
        }

    override val comments: MutableList<Comment> = LinkedList()

    final override val createDate: LocalDateTime = LocalDateTime.now()

    final override var modifyDate: LocalDateTime = LocalDateTime.now()

    protected fun refreshModifyDate() {
        modifyDate = LocalDateTime.now()
    }

}

abstract class AbstractComment(override val author: User, content: String) : Comment {
    override var content: String = content
        set(value) {
            field = value;
            refreshModifyDate()
        }

    protected fun refreshModifyDate() {
        modifyDate = LocalDateTime.now()
    }

    final override val createDate: LocalDateTime = LocalDateTime.now()
    final override var modifyDate: LocalDateTime

    init {
        this.modifyDate = createDate
    }
}

class AuthorizationInfo(val password: Byte) {

}

class CommentImpl(user: User, content: String) : AbstractComment(user, content)

class PostImpl(postName: String, author: User, content: String) : AbstractPost(postName, author, content)

class UserImpl(override var name: String) : User
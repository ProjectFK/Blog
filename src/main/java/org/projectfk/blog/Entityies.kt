package org.projectfk.blog

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "post")
class PostEntity (
        override var postName: String,
        override val author: User,
        override var content: String,
        override val createDate: LocalDateTime,
        override val modifyDate: LocalDateTime,
        comments: List<Comment>
) : Post, Serializable {

    @OneToMany
    override val comments: List<CommentEntity>

    init {
        val theList = LinkedList<CommentEntity>()
        comments.forEach {
            theList.add(CommentEntity(it))
        }

        this.comments = theList
    }

    @Id
    @GeneratedValue
    val id = 0

    constructor(post: Post) : this(
            post.postName,
            post.author,
            post.content,
            post.createDate,
            post.modifyDate,
            post.comments
    )
}

@Entity(name = "comment")
data class CommentEntity(
        override val author: User,
        override var content: String,
        override val createDate: LocalDateTime,
        override val modifyDate: LocalDateTime
) : Comment {

    constructor(comment: Comment) : this(
            comment.author,
            comment.content,
            comment.createDate,
            comment.modifyDate
    )

}



fun serialize(post: Post): Unit {
    val sending: PostEntity = PostEntity(post)
}
package org.projectfk.blog.data

import org.projectfk.blog.blogformat.BlogFormat
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "Blog")
class Blog(postName: String, content: String,
           @Column(nullable = false)
           val author: User,
           @Column(nullable = false)
           @Enumerated(EnumType.STRING)
           val format: BlogFormat) : Serializable {

//   JPA
    constructor() : this("", "", User(), BlogFormat.PLAIN_TEXT)

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0

    @Column(nullable = false)
    var postName: String = postName
        set(value) {
            field = value
            refreshModifyDate()
        }

    @Column(nullable = false)
    var rawContent: String = content
        set(value) {
            field = value
            refreshModifyDate()
        }

    @Column(nullable = false)
    val createDate: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var modifyDate: LocalDateTime = LocalDateTime.now()
        private set

    private fun refreshModifyDate() {
        modifyDate = LocalDateTime.now()
    }

    override fun toString(): String {
        return "Blog(author=$author, format=$format, id=$id, postName='$postName', rawContent='$rawContent', createDate=$createDate, modifyDate=$modifyDate)"
    }


}

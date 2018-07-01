package org.projectfk.blog.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.projectfk.blog.blogformat.BlogFormat
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "Blog")
class Blog(postName: String, content: String,
           author: User,
           format: BlogFormat) : Serializable {

//   JPA
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    constructor() : this("", "", User(), BlogFormat.PLAIN_TEXT)

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty(access = READ_ONLY)
    val id: Int = 0



    @Column(nullable = false)

    @JsonProperty
    val author = author


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)

    @JsonProperty
    val format = format


    @Column(nullable = false)

    @JsonProperty("title")
    var postTitle: String = postName
        set(value) {
            field = value
            refreshModifyDate()
        }


    @Column(nullable = false)

    @JsonProperty
    var rawContent: String = content
        set(value) {
            field = value
            refreshModifyDate()
        }


    @Column(nullable = false)

    @JsonProperty(access = READ_ONLY)
    val createDate: LocalDateTime = LocalDateTime.now()


    @Column(nullable = false)

//    JsonIgnore performs buggy on kotlin
    @JsonProperty(access = READ_ONLY)
    var modifyDate: LocalDateTime = LocalDateTime.now()
        private set

    private fun refreshModifyDate() {
        modifyDate = LocalDateTime.now()
    }

    override fun toString(): String =
            "Blog(author=$author, format=$format, id=$id, postTitle='$postTitle', rawContentLength='${rawContent.length}', createDate=$createDate, modifyDate=$modifyDate)"

    companion object {

        @JsonCreator
        @JvmStatic
        fun jsonEntry(
                @JsonProperty("id", required = true)
                id: Int): Blog {
            TODO()
        }

    }

}

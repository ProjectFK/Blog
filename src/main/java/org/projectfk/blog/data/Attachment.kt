package org.projectfk.blog.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "Attachment")
class Attachment(
        @Column(nullable = false)
        val fileName: String,
        @Column(nullable = false, length = 5)
        val region: String,

        @JoinColumn(name = "user")
        @ManyToOne
        val user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = -1

    @CreatedDate
    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()
}

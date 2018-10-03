package org.projectfk.blog.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "Attachment")
class Attachment(
        @Column(nullable = false)
        val fileName: String,

        @ManyToOne
        val region: StorageRegion
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = -1

    @CreatedDate
    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()

}

package org.projectfk.blog.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "TempUploadRequests")
class TempUploadRequests(
        @Id
        @Column(nullable = false, unique = true)
        val region: String,

        @JoinColumn(name = "user")
        @ManyToOne
        val user: User
) {

    @CreatedDate
    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()

}
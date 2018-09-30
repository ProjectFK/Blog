package org.projectfk.blog.data

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "StorageRegion")
class StorageRegion(
        @ManyToOne
        val user: User
) {

        @Id
        @Column
        @GeneratedValue(generator = "RandomString")
        val name: String = "_"

        @Column
        @CreatedDate
        val created: LocalDateTime = LocalDateTime.now()

}
package org.projectfk.blog.data

import org.projectfk.blog.common.RandomStringGenerator
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "StorageRegion")
class StorageRegion(
        @ManyToOne
        val user: User,
        @Id
        @Column
        val name: String
) {

        @Column
        @CreatedDate
        val created: LocalDateTime = LocalDateTime.now()

}
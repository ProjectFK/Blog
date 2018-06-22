package org.projectfk.blog.data

import org.hibernate.annotations.Type
import java.io.Serializable
import javax.persistence.*

@Entity(name = "User")
class User : Serializable{

    constructor(name: String, email: String) {
        if (name.length > 20) {
            throw IllegalArgumentException("the name should be shorter than 20 characters")
        }
        this.name = name
        this.email = email
    }

    @Column(length = 20)
    var name: String

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    @Type(type = "text")
    val email: String

}
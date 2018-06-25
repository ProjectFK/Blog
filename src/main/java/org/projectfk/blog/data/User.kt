package org.projectfk.blog.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.services.UserService
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.persistence.*

@Component
@Entity(name = "User")
class User : Serializable{

    internal constructor(
            name: String
    ) {
        this.name = name
    }

//    JPA
    constructor() {
        this.name = "_"
    }

    @Column(length = 20, nullable = false)
    var name: String

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = 0

    override fun toString(): String {
        return "User(name='$name', id=$id')"
    }
    
    companion object {

        @JsonCreator
        fun JsonFactoryEntry(
                @JsonProperty("id") id: Int
        ): User {
            if (id < 0) throw IllegalParametersException("invalid id")
            return UserService.UserService.findByID(id).orElseThrow {
                IllegalParametersException("there's no such spamUser with id $id in database")
            }
        }

    }

}
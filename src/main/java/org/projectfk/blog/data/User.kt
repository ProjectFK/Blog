package org.projectfk.blog.data

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.hibernate.annotations.CreationTimestamp
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.services.UserService
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Component
@Entity(name = "User")
@Inheritance(strategy = InheritanceType.JOINED)
class User : Serializable, org.springframework.security.core.userdetails.User {

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    internal constructor(
            name: String,
            password: String,
            authorities: Collection<GrantedAuthority> = emptyList()
    ) : super(name, password, authorities)

    //    JPA
    constructor() : this("_", "_")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)

    @JacksonXmlProperty(isAttribute = true)
    @JsonIgnoreProperties
    val id: Int = 0

    @CreationTimestamp
    val timeJoined: LocalDateTime = LocalDateTime.now()

    override fun toString(): String = "User(name='$username', id=$id')"

    @JsonAnyGetter
    fun formatOut(): Map<String, Any> = mapOf(
            "id" to id,
            "user_name" to username
    )

    companion object {

        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun JsonIDEntry(
                id: Int
        ): User {
            if (id < 0) throw IllegalParametersException("invalid id")
            return UserService.UserService.findByID(id).orElseThrow {
                IllegalParametersException("there's no such user with id: $id in database")
            }
        }


    }

}
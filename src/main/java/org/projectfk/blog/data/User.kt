package org.projectfk.blog.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.hibernate.annotations.CreationTimestamp
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.services.UserService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Component
@Entity(name = "User")
@Table(name = "user")
class User : Serializable, UserDetails {
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    internal constructor(
            name: String,
            password: String
    ) {
        username = name
        this.password = password
    }

    //    JPA
    constructor() : this("_", "_")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)

    @JacksonXmlProperty(isAttribute = true)
    @JsonIgnoreProperties
    val id: Int = 0

    @CreationTimestamp
    @Column

    @JsonIgnore
    val timeJoined: LocalDateTime = LocalDateTime.now()

    @Column
    private var isEnable = true

    @Column
    private val username: String

    @Column(columnDefinition = "CHAR(60)")
    @JsonIgnore
    private val password: String

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    @JsonIgnore
    override fun isEnabled(): Boolean = this.isEnable

    override fun getUsername(): String = username

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @JsonIgnore
    override fun getPassword(): String = password

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = isEnable

    override fun toString(): String = "User(name='$username', id=$id')"

    override fun equals(other: Any?): Boolean {
        if (other == this) return true
        if (!(other is User)) return false
        if (other.id != 0) return this.id == other.id
        return this.username == other.username
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }

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

        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun JsonIDEntry(
                id: String
        ): User = UserService.UserService.loadUserByUsername(id)
//        IllegalParametersException("there's no such user with id: $id in database")


    }

}
package org.projectfk.blog.services

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.ForbiddenException
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.debugIfEnable
import org.projectfk.blog.data.User
import org.projectfk.blog.data.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.net.URI
import java.util.*

@Service
class UserService : UserDetailsService {

    private val logger: Log by lazy { LogFactory.getLog(UserService::class.java) }

    @Value("\${avatar.default}")
    lateinit var defaultAvatarPath: String

    @Autowired
    private lateinit var userRepo: UserRepo

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    init {
        INSTANCE = this
    }

    fun findByID(id: Int): Optional<User> = userRepo.findById(id)

    fun saveUser(user: User): User = userRepo.save(user)

    override fun loadUserByUsername(username: String): User {
        logger.debugIfEnable { "loading user with user name: $username" }
        val users = userRepo.findByUsername(username).iterator()
        if (!users.hasNext()) {
            val notFoundException = UsernameNotFoundException("User with username: $username not found")
            logger.debugIfEnable(notFoundException) { "user $username do not found" }
            throw notFoundException
        }
        val value = users.next()
        if (users.hasNext()) {
            val message = "multiple user with same name, user name is: $username"
            val assertionError = AssertionError(message)
            logger.fatal(message, assertionError)
            throw assertionError
        }
        return value
    }

    /**
     * Registry a new User
     * @throws IllegalParametersException when parameter given do not match requirement
     */
    fun registryNewUser(name: String, password: String): User {
        if (!validateUserName(name))
            throw IllegalParametersException("Naming do not match requirement")
        if (!validatePassword(password))
            throw IllegalParametersException("Password do not match requirement")
        logger.info("Registering user: $name")
        if (userRepo.findByUsername(name).iterator().hasNext())
            throw ForbiddenException("user with name $name already exists")
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(name, encodedPassword)
        return saveUser(user)
    }

    fun updateUserProfile(userProfile: UserProfile): User {
        TODO("Unfinished")
    }

    @Value("\${user.password.check.regexp}")
    private lateinit var _passwordCheckRegex: String


    companion object {

        /**
         * Expose for Jackson User Deserialize Entry
         */
        @JvmStatic
        lateinit var INSTANCE: UserService
            private set

        fun validateUserName(name: String): Boolean = name.length in 2..20

        private var passwordCheckRegex: Regex? = null
        private var usingRegex: Boolean? = null

        fun validatePassword(pwd: String): Boolean {
            if (usingRegex == null) {
                if (INSTANCE._passwordCheckRegex.equals("false", true)) {
                    usingRegex = false
                } else {
                    usingRegex = true
                    passwordCheckRegex = Regex(INSTANCE._passwordCheckRegex)
                }
            }
            return if (usingRegex!!) {
                passwordCheckRegex!!.matches(pwd)
            } else {
                true
            }
        }

    }

}

class UserProfile {
    val origin: User
    val passwordEncoded: String?
    val username: String?
    val avatar: URI?

    constructor(origin: User, passwordEncoded: String? = null, username: String? = null, avatar: URI? = null) {
        this.origin = origin

        if (passwordEncoded == null || UserService.INSTANCE.passwordEncoder.matches(passwordEncoded, origin.password))
            this.passwordEncoded = null
        else this.passwordEncoded = passwordEncoded

        if (username != null && origin.username !== username) this.username = username
        else this.username = null;

        if (avatar != null && origin.avatarPath != avatar.rawPath) this.avatar = avatar
        else this.avatar = null
    }
}

fun supplyNotFound(name: String): NotFoundException = NotFoundException("User with username: $name not found")

fun supplyNotFound(id: Int): NotFoundException = NotFoundException("User with id: $id not found")
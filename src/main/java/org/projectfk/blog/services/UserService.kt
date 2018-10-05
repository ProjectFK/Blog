package org.projectfk.blog.services

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.ForbiddenException
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.debugIfEnable
import org.projectfk.blog.data.Attachment
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
    private lateinit var passwordEncoder: PasswordEncoder

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

    fun checkAndEncodePassword(password: String): String {
        if (!validatePassword(password))
            throw IllegalParametersException("Password do not match requirement")
        return passwordEncoder.encode(password)
    }

    fun rawPasswordMatches(rawPassword: String, target: String): Boolean {
        if (!validatePassword(target))
            return false
        return passwordEncoder.matches(rawPassword, target)
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
        val user = findByID(userProfile.origin.id)
        TODO("waiting for avatar service")
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

class UserProfile(val origin: User, password: String? = null, username: String? = null, avatar: Attachment? = null) {
    val passwordEncoded: String? = null
    val username: String? = null
    val avatar: URI? = null

    init {
        if (username != null && origin.username !== username) {
            if (!UserService.validateUserName(username))
                throw IllegalParametersException("Password do not match requirement")
//            this.username = username
        } else {
//            this.username = null
        }

        if (password == null || UserService.INSTANCE.rawPasswordMatches(origin.password, password))
//            this.passwordEncoded = null
        else
//            this.passwordEncoded = UserService.INSTANCE.checkAndEncodePassword(password)

        TODO("WAITING FOR AVATAR TO FINISH")
//        if (avatar != null && origin.avatarPath != avatar.rawPath)
//            this.avatar = avatar
//        else
//            this.avatar = null
    }
}

fun supplyNotFound(name: String): NotFoundException = NotFoundException("User with username: $name not found")

fun supplyNotFound(id: Int): NotFoundException = NotFoundException("User with id: $id not found")
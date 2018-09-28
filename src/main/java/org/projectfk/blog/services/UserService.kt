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
import org.springframework.context.annotation.PropertySource
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@PropertySource("classpath:common_settings_config.properties")
class UserService : UserDetailsService {

    private val logger: Log by lazy { LogFactory.getLog(UserService::class.java) }

    @Autowired
    private lateinit var userRepo: UserRepo

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        UserService = this
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

    @Value("\${user.password.check.regexp}")
    private lateinit var _passwordCheckRegex: String


    companion object {

        /**
         * Expose for Jackson User Deserialize Entry
         */
        @JvmStatic
        lateinit var UserService: UserService
            private set

        fun validateUserName(name: String): Boolean = name.length in 2..20

        private var passwordCheckRegex: Regex? = null
        private var usingRegex: Boolean? = null

        fun validatePassword(pwd: String): Boolean {
            if (usingRegex == null) {
                if (UserService._passwordCheckRegex.equals("false", true)) {
                    usingRegex = false
                } else {
                    usingRegex = true
                    passwordCheckRegex = Regex(UserService._passwordCheckRegex)
                }
            }
            if (usingRegex!!) {
                return passwordCheckRegex!!.matches(pwd)
            } else {
                return true
            }
        }

    }

}

fun Int.findUserAsThisIsAnIDName(): Optional<User> = UserService.UserService.findByID(this)

fun supplyNotFound(name: String): NotFoundException
    = NotFoundException("User with username: $name not found")

fun supplyNotFound(id: Int): NotFoundException
    = NotFoundException("User with id: $id not found")
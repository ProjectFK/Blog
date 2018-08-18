package org.projectfk.blog.services

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.data.User
import org.projectfk.blog.data.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
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
        val users = userRepo.findByUsername(username).toList()
        if (users.isEmpty()) {
            val notFoundException = UsernameNotFoundException("User with username: $username do not exists")
            logger.debugIfEnable(notFoundException) { "user $username do not found" }
            throw notFoundException
        }
        if (users.size > 1) {
            val message = "multiple user with same name, user name is: $username"
            val assertionError = AssertionError(message)
            logger.fatal(message, assertionError)
            throw assertionError
        }
        return users[0]
    }

    fun registryNewUser(name: String, password: String): User {
        logger.info("Registering user: $name")
        if (name.length > 20 || name.isEmpty() || name == "_" )
            throw IllegalParametersException(
                    "the name should be shorter than 20 characters and bigger than 1 character and not a \"_\"; " +
                            "name given: $name"
            )
        try {
            loadUserByUsername(name)
            throw IllegalParametersException("user with name $name already exists")
        } catch (e: UsernameNotFoundException) {}
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(name, encodedPassword)
        saveUser(user)
        return user
    }

    companion object {

        /**
         * Expose for Jackson User Deserialize Entry
         */
        internal lateinit var UserService: UserService

    }

}

//better performance for more bsting
//No need to lazy load throwable
inline fun Log.debugIfEnable(throwable: Throwable? = null, message: () -> String): Unit {
    if (isDebugEnabled) debug(message.invoke(), throwable)
}

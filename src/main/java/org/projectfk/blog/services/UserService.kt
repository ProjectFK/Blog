package org.projectfk.blog.services

import org.projectfk.blog.data.User
import org.projectfk.blog.data.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {



    @Autowired
    private lateinit var userRepo: UserRepo

    init {
        UserService = this
    }

    fun findByID(id: Int): Optional<User> = userRepo.findById(id)

    fun findByName(name: String): List<User> = userRepo.findByName(name).toList()

    fun saveUser(user: User): User = userRepo.save(user)

    fun registryNewUser(name: String): User {
        if (name.length > 20 || name.isEmpty() || name == "_")
            throw IllegalArgumentException(
                    "the name should be shorter than 20 characters and bigger than 1 character and not a \"_\"; " +
                            "name given: $name"
            )
        val user = User(name)
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
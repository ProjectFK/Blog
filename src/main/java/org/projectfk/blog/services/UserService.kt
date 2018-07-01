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

    constructor() {
        UserService = this;
    }

    private constructor(any: Any?)

    fun findByID(id: Int): Optional<User> = userRepo.findById(id)

    fun findByName(name: String): List<User> = userRepo.findByName(name).toList()

    fun saveUser(user: User): User = userRepo.save(user)

    fun registryNewUser(name: String): User {
        if (name.length > 20 || name.isEmpty() || name == "_")
            throw IllegalArgumentException(
                    "the name should be shorter than 20 characters and bigger than 1 character and not a \"_\"; " +
                            "name qgiven: $name"
            )
        val user = User(name)
        saveUser(user)
        return user
    }

    companion object {

        lateinit internal var UserService: UserService

    }


}
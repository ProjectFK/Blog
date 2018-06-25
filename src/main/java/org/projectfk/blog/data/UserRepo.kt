package org.projectfk.blog.data

import org.springframework.data.repository.CrudRepository

interface UserRepo : CrudRepository<User, Int> {

    fun findByName(name: String): Iterable<User>

}

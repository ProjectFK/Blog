package org.projectfk.blog.data

import org.springframework.data.repository.CrudRepository

interface UserRepo : CrudRepository<User, Int> {

//    fun findByUsername(name: String): Iterable<User>

}

package org.projectfk.blog.data

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface UserRepo : CrudRepository<User, Int> {

    fun findByUsername(name: String): Iterable<User>

}

@Repository
interface PostRepo : CrudRepository<Blog, Int> {

    fun findByAuthor(user: User) : Iterable<Blog>

    fun findAllByOrderByModifyDateDesc(): List<Blog>

}
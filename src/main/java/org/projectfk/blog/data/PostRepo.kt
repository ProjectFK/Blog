package org.projectfk.blog.data

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepo : CrudRepository<Blog, Int> {

    fun findByAuthor(user: User) : Iterable<Blog>

}
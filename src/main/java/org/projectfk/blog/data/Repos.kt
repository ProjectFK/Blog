package org.projectfk.blog.data

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepo : CrudRepository<User, Int> {

    fun findByUsername(name: String): Iterable<User>

}

@Repository
interface BlogRepo : CrudRepository<Blog, Int> {

    fun findByAuthor(user: User) : Iterable<Blog>

    fun findAllByOrderByModifyDateDesc(): List<Blog>

    fun deleteByid(id: Int): Optional<Blog>

}

@Repository
interface AttachmentRepo : CrudRepository<Attachment, String> {

    fun findByRegion(region: StorageRegion): List<Attachment>

}

@Repository
interface StorageRegionRepo: CrudRepository<StorageRegion, String>
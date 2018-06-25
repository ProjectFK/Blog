package org.projectfk.blog.services

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.PostRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BlogService {

    private val logger: Log = LogFactory.getLog(BlogService::class.java)

    @Autowired
    private lateinit var repo: PostRepo

    fun listAllBlogs(): List<Blog> = repo.findAll().toList()

    fun saveBlog(content: Blog): Blog = repo.save(content)

    fun saveBlogs(contents: List<Blog>): List<Blog> = repo.saveAll(contents).toList()

    fun delete(blog: Blog): Unit = repo.delete(blog)

    fun deleteAlot(blogs: List<Blog>): Unit = repo.deleteAll(blogs)

    fun blogByID(id: Long): Optional<Blog> = repo.findById(id)

    fun blogByAuthor(author: User): List<Blog> = repo.findByAuthor(author).toList()

    fun createBlog(creator: () -> Blog): Blog = saveBlog(creator.invoke())

}
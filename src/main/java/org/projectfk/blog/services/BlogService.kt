package org.projectfk.blog.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.PostRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.PostConstruct

@Service
class BlogService {

    @Autowired
    private lateinit var repo: PostRepo

    @PostConstruct
    fun linkToStaticField() {
        _service.compareAndExchange(null, this);
    }

    fun listAllBlogs(): List<Blog> = repo.findAll().toList()

    fun saveBlog(content: Blog): Blog = repo.save(content)

    fun saveBlogs(contents: List<Blog>): List<Blog> = repo.saveAll(contents).toList()

    fun delete(blog: Blog): Unit = repo.delete(blog)

    fun deleteAlot(blogs: List<Blog>): Unit = repo.deleteAll(blogs)

    fun blogByID(id: Int): Optional<Blog> = repo.findById(id)

    fun blogByAuthor(author: User): List<Blog> = repo.findByAuthor(author).toList()

    fun createBlog(content: Blog): Blog = saveBlog(content)

    companion object {
        val service: BlogService
            get() {
                val result = _service.get()
                if (result != null) return result
                throw IllegalStateException("Blog service is not initialized")
            }

        private var _service: AtomicReference<BlogService> = AtomicReference()

        @JsonCreator
        fun jsonCreatorEntry(
                @JsonProperty("id", required = true)
                id: Int
        ): Blog = service.blogByID(id).orElseThrow {
            IllegalParametersException("there's no such blog with id: $id in database")
        }

    }

}
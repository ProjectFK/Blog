package org.projectfk.blog.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.PostRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.PostConstruct

@Service
class BlogService {

    private val logger: Log = LogFactory.getLog(BlogService::class.java)

    @Autowired
    private lateinit var repo: PostRepo

    @PostConstruct
    fun linkToStaticField() {
        _service = this;
    }

    fun listAllBlogs(): List<Blog> = repo.findAll().toList()

    fun saveBlog(content: Blog): Blog = repo.save(content)

    fun saveBlogs(contents: List<Blog>): List<Blog> = repo.saveAll(contents).toList()

    fun delete(blog: Blog): Unit = repo.delete(blog)

    fun deleteAlot(blogs: List<Blog>): Unit = repo.deleteAll(blogs)

    fun blogByID(id: Int): Optional<Blog> = repo.findById(id)

    fun blogByAuthor(author: User): List<Blog> = repo.findByAuthor(author).toList()

    fun createBlog(creator: () -> Blog): Blog {
        val content = creator.invoke()
        return saveBlog(content)
    }

        companion object {
        val service: BlogService
            get() {
                if (_service != null) return _service as BlogService
                lock.lock()
                try {
                    if (_service == null)
                        throw IllegalStateException("Blog service is not initalized by Spring")
                    return _service as BlogService
                } finally {
                    lock.unlock()
                }
            }

        private var _service: BlogService? = null

        private val lock: ReentrantLock = ReentrantLock(true)

        @JsonCreator
        fun jsonCreatorEntry(
                @JsonProperty("id", required = true)
                id: Int
        ): Blog = service.blogByID(id).orElseThrow {
            IllegalParametersException("there's no such blog with id: $id in database")
        }

    }

}
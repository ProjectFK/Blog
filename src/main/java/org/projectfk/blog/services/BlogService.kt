package org.projectfk.blog.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.BlogRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.PostConstruct

@Service
class BlogService {

    @Autowired
    private lateinit var repo: BlogRepo

    @PostConstruct
    fun linkToStaticField() {
        _service.compareAndSet(null, this)
    }

    fun listAllBlogs(): List<Blog> = repo.findAllByOrderByModifyDateDesc().toList()

    private fun saveBlog(content: Blog): Blog {
        return repo.save(content)
    }

    fun delete(blog: Blog): Unit = repo.delete(blog)

    fun blogByID(id: Int): Optional<Blog> = repo.findById(id)

    fun blogByAuthor(author: User): List<Blog> = repo.findByAuthor(author).toList()

    fun deleteBlogByID(id: Int): Optional<Blog> = repo.deleteByid(id)

    fun createBlog(content: Blog): Blog {
        assert(!content.alreadyLoaded())
        return saveBlog(content)
    }

    fun updateBlog(target: Blog): Blog {
        assert(target.alreadyLoaded())
        return saveBlog(target)
    }

    companion object {

        @JvmStatic
        val service: BlogService
            get() {
                val result = _service.get()
                if (result != null) return result
                throw IllegalStateException("Blog service is not initialized")
            }

        private var _service: AtomicReference<BlogService> = AtomicReference()

        @JsonCreator
        fun jsonCreatorEntry(
                @JsonProperty("id")
                id: Int
        ): Blog = service.blogByID(id).orElseThrow {
            NotFoundException("there's no such blog with id: $id in database")
        }

    }

}
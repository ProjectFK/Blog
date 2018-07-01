package org.projectfk.blog.controller

import org.projectfk.blog.blogformat.BlogFormat
import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.common.KnownException
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.data.Blog
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

@RestController
@RequestMapping("/blog/")
class BlogController {

    @Autowired
    private lateinit var blogService: BlogService

    @Autowired
    private lateinit var userService: UserService
    
    val integer = AtomicInteger(0)

    @GetMapping()
    fun getAll(): ResultBean<List<Blog>> = ResultBean(blogService.listAllBlogs())

    @GetMapping("/{id}")
    fun getByID(@PathVariable("id") id:Int): ResultBean<Blog> {
        if (id <= 0) throw IllegalParametersException("id should not be smaller than 0 or equals 1")
        val result = blogService.blogByID(id)
        if (result.isPresent) return ResultBean(result.get())
        else throw NotFoundException()
    }

    @GetMapping("/exception")
    fun exception(): Nothing = throw KnownException("Here's your error")

    val spamUser by lazy {
        userService.registryNewUser("Random spam")
    }

    @GetMapping("/spam")
    fun generateRandomBlog(): ResultBean<Blog> {
        val blog = Blog("spam ${integer.incrementAndGet()}", "nothing", spamUser, BlogFormat.PLAIN_TEXT)
        return ResultBean(blogService.saveBlog(blog))
    }

    @PostMapping
    fun createBlog(
            @RequestBody
            postBody: Blog
    ): ResponseEntity<ResultBean<Blog>> {
        val result = blogService.createBlog { postBody }
        return ResponseEntity
                .created(URI.create("/blog/${result.id}"))
                .body(ResultBean(result))
    }


}
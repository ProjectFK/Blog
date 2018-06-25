package org.projectfk.blog.controller

import org.projectfk.blog.blogformat.BlogFormat
import org.projectfk.blog.common.KnownException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.common.State
import org.projectfk.blog.common.StateResultBean
import org.projectfk.blog.data.Blog
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicInteger

@RestController
@RequestMapping("/blog/")
class BlogController {

    @Autowired
    private lateinit var blogService: BlogService

    @Autowired
    private lateinit var userService: UserService
    
    val integer = AtomicInteger(0)

    @GetMapping
    fun getAll(): ResultBean<List<Blog>> = ResultBean(blogService.listAllBlogs())

    @GetMapping("/{id}")
    fun getByID(@PathVariable("id") id:Int): ResultBean<Blog?> = ResultBean(blogService.blogByID(id.toLong()).orElse(null))

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
    ): ResponseEntity<StateResultBean> {
        TODO("IMPLANTATION NOT APPLIED")
        return ResponseEntity.ok(StateResultBean(State.ErrorState))
    }

    @ExceptionHandler((KnownException::class))
    fun knownExceptionHandler(exception: KnownException): StateResultBean = StateResultBean(State.ExceptionState(exception))

}
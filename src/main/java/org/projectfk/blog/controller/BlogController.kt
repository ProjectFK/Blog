package org.projectfk.blog.controller

import org.projectfk.blog.common.IllegalParametersException
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.User
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("api/blog/")
class BlogController {

    @Autowired
    private lateinit var blogService: BlogService

    @Autowired
    private lateinit var userService: UserService

    @GetMapping
    fun getAll(): ResultBean<List<Blog>> = ResultBean(blogService.listAllBlogs())

    @GetMapping("/{id:[0-9]}")
    fun getByID(@PathVariable("id") id: Int): ResultBean<Blog> {
        if (id <= 0) throw IllegalParametersException("id should not be smaller than 0 or equals 1")
        val result = blogService.blogByID(id)
        if (result.isPresent) return ResultBean(result.get())
        else throw NotFoundException()
    }

    @PostMapping
    fun createBlog(
            @RequestBody
            blog: inputBlogDTO
    ): ResponseEntity<ResultBean<Blog>> {
//        TODO: user pass from Spring Security
        val result = blogService.createBlog(blog.toBlog(userService.findByID(1).orElseThrow { throw IllegalStateException() }))
        return ResponseEntity
                .created(URI.create("/blog/${result.id}"))
                .body(ResultBean(result))
    }

    @PutMapping
    fun updateBlog(
            @RequestBody
            blog: updateBlogDTO
    ): ResultBean<Blog> =
            blogService
                    .blogByID(blog.id)
                    .map {
//                        TODO: User pass from Spring Security
                        validateUserOrThrow(it, userService.findByID(1).orElse(null))
                        it.swap(blog)
                        ResultBean(it)
                    }
                    .orElseThrow { NotFoundException("not found blog with id: ${blog.id}") }

    private fun validateUserOrThrow(blog: Blog, user: User): Blog {
        if (blog.author != user)
            throw AccessDeniedException("operation user do not match blog author")
        return blog
    }

    @GetMapping("/listByAuthor")
    fun listBlogByUser(
            @PathVariable("author")
            author: User
    ): ResultBean<List<Blog>> = ResultBean(blogService.blogByAuthor(author))

}

sealed class IInputBlogDTO(val content: String, val title: String)

class inputBlogDTO(content: String, title: String) : IInputBlogDTO(content, title)

class updateBlogDTO(val id: Int, content: String, title: String) : IInputBlogDTO(content, title)

fun inputBlogDTO.toBlog(author: User): Blog = Blog(author, this.title, this.content)

fun Blog.swap(target: updateBlogDTO): Blog {
    this.content = target.content
    this.title = target.title
    return this
}
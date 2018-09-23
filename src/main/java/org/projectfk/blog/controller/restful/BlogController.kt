package org.projectfk.blog.controller.restful

import org.projectfk.blog.common.*
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.Tag
import org.projectfk.blog.data.User
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.projectfk.blog.services.supplyNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("api/blog/")
open class BlogController {

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
        else throw NotFoundException("Blog with id: $id not found")
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    open fun createBlog(
            @RequestBody
            blog: inputBlogDTO,
            auth: Authentication
    ): ResponseEntity<ResultBean<CreatedResponseBody<Blog>>> {
        val result = blogService.createBlog(blog.toBlog(auth.principal as User))
        val url = URI.create("/blog/${result.id}")
        return created(url, result)
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    open fun updateBlog(
            @RequestBody
            blog: updateBlogDTO,
            auth: Authentication
    ): ResultBean<Blog> =
            blogService
                    .blogByID(blog.id)
                    .map {
                        validateUserOrThrow(it, auth.principal as User)
                        it.swap(blog)
                        blogService.updateBlog(it)
                        ResultBean(it)
                    }
                    .orElseThrow { NotFoundException("not found blog with id: ${blog.id}") }

    private fun validateUserOrThrow(blog: Blog, user: User): Blog {
        if (blog.author != user)
            throw ForbiddenException("you do not have permission to edit this post")
        return blog
    }

    @GetMapping("/listByAuthor")
    fun listBlogByUser(
            @RequestParam("author")
            name: String
    ): ResultBean<List<Blog>> {
        val author = try {
            val id = Integer.parseInt(name)
            userService
                    .findByID(id)
                    .orElseThrow { supplyNotFound(id) }
        } catch (exception: NumberFormatException) {
            userService
                    .loadUserByUsername(name)
        }
        return ResultBean(blogService.blogByAuthor(author))
    }

}

sealed class IInputBlogDTO(val content: String, val title: String, val tag: String)

class inputBlogDTO(content: String, title: String, tag: String) : IInputBlogDTO(content, title, tag)

class updateBlogDTO(val id: Int, content: String, title: String, tag: String) : IInputBlogDTO(content, title, tag)

fun inputBlogDTO.toBlog(author: User): Blog {
    val tag: Tag
    try {
        tag = Tag.valueOf(this.tag)
    } catch (e: IllegalArgumentException) {
        throw IllegalParametersException("${this.tag} is not a valid tag")
    }
    return Blog(author, this.title, this.content, tag)
}

fun Blog.swap(target: updateBlogDTO): Blog {
    this.content = target.content
    this.title = target.title
    try {
        this.tag = Tag.valueOf(target.tag)
    } catch (e: IllegalArgumentException) {
        throw IllegalParametersException("${target.tag} is not a valid tag")
    }
    return this
}
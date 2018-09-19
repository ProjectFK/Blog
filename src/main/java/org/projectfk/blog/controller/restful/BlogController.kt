package org.projectfk.blog.controller.restful

import org.projectfk.blog.common.*
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.Tag
import org.projectfk.blog.data.User
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.projectfk.blog.services.findUserAsThisIsAnIDName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
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
        else throw NotFoundException("Blog with id: $id not found")
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createBlog(
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
                        blogService.updateBlog(it)
                        ResultBean(it)
                    }
                    .orElseThrow { NotFoundException("not found blog with id: ${blog.id}") }

    private fun validateUserOrThrow(blog: Blog, user: User): Blog {
        if (blog.author != user)
            throw ForbiddenException("operating user do not match blog author")
        return blog
    }

    @GetMapping("/listByAuthor")
    fun listBlogByUser(
            @RequestParam("author")
            author: Int
    ): ResultBean<List<Blog>> =
            ResultBean(
                    blogService
                            .blogByAuthor(
                                    author
                                            .findUserAsThisIsAnIDName()
                                            .orElseThrow {
                                                NotFoundException("user with id: $author do not found")
                                            }
                            )
            )

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
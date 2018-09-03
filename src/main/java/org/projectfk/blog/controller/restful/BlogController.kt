package org.projectfk.blog.controller.restful

import org.projectfk.blog.common.*
import org.projectfk.blog.data.Blog
import org.projectfk.blog.data.User
import org.projectfk.blog.services.BlogService
import org.projectfk.blog.services.UserService
import org.projectfk.blog.services.findUserAsThisIsAnIDName
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
    ): ResponseEntity<ResultBean<CreatedResponseBody<Blog>>> {
//        TODO: user pass from Spring Security
        val result = blogService.createBlog(blog.toBlog(
                userService
                        .findByID(1)
                        .orElseThrow { throw IllegalStateException() }
        ))
        val url = URI.create("/blog/${result.id}")
        return created(url, result)
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
                        blogService.updateBlog(it)
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

sealed class IInputBlogDTO(val content: String, val title: String)

class inputBlogDTO(content: String, title: String) : IInputBlogDTO(content, title)

class updateBlogDTO(val id: Int, content: String, title: String) : IInputBlogDTO(content, title)

fun inputBlogDTO.toBlog(author: User): Blog = Blog(author, this.title, this.content)

fun Blog.swap(target: updateBlogDTO): Blog {
    this.content = target.content
    this.title = target.title
    return this
}
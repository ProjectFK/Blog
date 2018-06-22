package org.projectfk.blog.controller

import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.data.Blog
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/blog/")
class BlogController {

    @GetMapping
    fun getAll(): ResultBean<Collection<Blog>> = ResultBean(Collections.emptyList<Blog>())

}
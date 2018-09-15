package org.projectfk.blog.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonUnwrapped
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

enum class Tag {
    life,
    tech;
}
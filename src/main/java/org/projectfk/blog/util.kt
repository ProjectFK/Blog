package org.projectfk.blog

fun fasterStringCompare(one: String, another: String): Boolean {
    if (one.length != another.length) return false
    return one.hashCode() == another.hashCode()
}
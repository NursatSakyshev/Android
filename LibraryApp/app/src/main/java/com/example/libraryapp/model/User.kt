package com.example.libraryapp.model

interface Printable {
    fun printInfo()
}

abstract class User(val name: String): Printable {
    abstract val maxBooks: Int
}

class Student(name: String): User(name) {
    override val maxBooks = 3
    override fun printInfo() {
        println("I am a student: $name")
    }
}

class Teacher(name: String): User(name) {
    override val maxBooks = 10
    override fun printInfo() {
        println("I am a teacher: $name")
    }
}
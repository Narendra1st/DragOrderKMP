package com.example.dragorderkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
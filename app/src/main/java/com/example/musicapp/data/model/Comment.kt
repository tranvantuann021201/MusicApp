package com.example.musicapp.data.model

data class Comment(
    val postId: String,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

data class Post(
    val title: String,
    val body: String,
    val userId: Int
)

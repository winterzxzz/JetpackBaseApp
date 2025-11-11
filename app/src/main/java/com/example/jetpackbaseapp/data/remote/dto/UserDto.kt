package com.example.jetpackbaseapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String
)

data class UsersResponse(
    @SerializedName("data")
    val data: List<UserDto>
)

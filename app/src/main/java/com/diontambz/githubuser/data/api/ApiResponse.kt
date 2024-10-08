package com.diontambz.githubuser.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseSearch(

    @field:SerializedName("items")
    val items: ArrayList<SimpleUser>
)

data class User(

    @field:SerializedName("bio")
    val bio: String?,

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("blog")
    val blog: String?,

    @field:SerializedName("followers")
    val followers: Int,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    @field:SerializedName("html_url")
    val htmlUrl: String,

    @field:SerializedName("following")
    val following: Int,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("company")
    val company: String?,

    @field:SerializedName("location")
    val location: String?,

    @field:SerializedName("public_repos")
    val publicRepos: Int,

    @field:SerializedName("public_gists")
    val publicGists: Int,
)

data class SimpleUser(

    @field:SerializedName("avatar_url")
    val avatarUrl: String,

    @field:SerializedName("login")
    val login: String
)



package com.diontambz.githubuser.data.remote

import com.diontambz.githubuser.data.remote.response.ResponseSearch
import com.diontambz.githubuser.data.remote.response.SimpleUser
import com.diontambz.githubuser.data.remote.response.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun searchUsername(
        @Header("Authorization") token: String,
        @Query("q") q: String
    ): ResponseSearch

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): User

    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): ArrayList<SimpleUser>

    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): ArrayList<SimpleUser>
}
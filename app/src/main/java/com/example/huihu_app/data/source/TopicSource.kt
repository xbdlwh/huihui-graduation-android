package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateTopicRequest
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.data.model.TopicLikeRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TopicSource {
    @GET("/topic")
    suspend fun topics(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): ApiResponse<List<Topic>>

    @GET("/topic/comment/{topic_id}")
    suspend fun comments(
        @Header("Authorization") token: String,
        @Path("topic_id") topicId: Int
    ): ApiResponse<List<Topic>>

    @Multipart
    @POST("/upload")
    suspend fun upload(@Part files: List<MultipartBody.Part>): ApiResponse<List<String>>

    @POST("/topic")
    suspend fun createTopic(
        @Header("Authorization") token: String,
        @Body request: CreateTopicRequest
    ): ApiResponse<Unit>

    @POST("/topic/like")
    suspend fun likeTopic(
        @Header("Authorization") token: String,
        @Body request: TopicLikeRequest
    ): ApiResponse<Unit?>
}

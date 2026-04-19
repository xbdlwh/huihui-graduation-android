package com.example.huihu_app

import androidx.navigation3.runtime.NavKey
import com.example.huihu_app.data.model.Suggestion
import com.example.huihu_app.data.model.Topic
import kotlinx.serialization.Serializable

sealed interface Nav {
    @Serializable
    object Login: Nav, NavKey
    @Serializable
    object Register: Nav, NavKey
    @Serializable
    object CreateTopic: Nav, NavKey
    @Serializable
    object EditProfile: Nav, NavKey
    @Serializable
    object FoodTrack: Nav, NavKey
    @Serializable
    object FoodLiked: Nav, NavKey
    @Serializable
    object TopicManage: Nav, NavKey
    @Serializable
    object Suggestion: Nav, NavKey
    @Serializable
    object AddSuggestion: Nav, NavKey
    @Serializable
    data class ImagePreview(val images: List<String>, val index: Int): Nav, NavKey
    @Serializable
    data class SuggestionDetail(val suggestion: com.example.huihu_app.data.model.Suggestion): Nav, NavKey
    @Serializable
    data class TopicDetail(val topic: Topic): Nav, NavKey
    @Serializable
    data class WriteComment(val commentToId: Int): Nav, NavKey
    @Serializable
    object NewPerson: Nav, NavKey
    @Serializable
    data class FoodAttr(val foodId: Int): Nav, NavKey
    @Serializable
    object Home: Nav, NavKey
    @Serializable
    object Splash: Nav, NavKey
    @Serializable
    object UserProfile: Nav, NavKey
}

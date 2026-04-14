package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodAttribute(
    val food_id: Int,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val fiber: Double,
    val sugar: Double,
    val sodium: Double,
    val serving_size: String,
    val is_vegetarian: Boolean,
    val is_vegan: Boolean,
    val is_gluten_free: Boolean,
    val allergens: String?,
    val ingredients: String?
)

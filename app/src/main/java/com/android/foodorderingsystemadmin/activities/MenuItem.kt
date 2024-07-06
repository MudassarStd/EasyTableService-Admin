package com.android.foodorderingsystemcustomer.model


data class MenuItem(
    val docId : String,
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodDescription: String? = null,
    val foodImage: String? = null,
    val foodIngredient: List<String>? = null,
)

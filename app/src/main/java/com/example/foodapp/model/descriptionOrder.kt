package com.example.foodapp.model

import org.json.JSONArray

data class descriptionOrder (val orderId: Int,
                             val resName: String,
                             val orderDate: String,
                             val foodItem: JSONArray
)

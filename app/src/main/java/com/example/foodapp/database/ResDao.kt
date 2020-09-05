package com.example.foodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao{

    @Insert
    fun insertRestaurant(restaurantEntity: ResEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: ResEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<ResEntity>

    @Query("SELECT * FROM restaurants WHERE id = :resId")
    fun getRestaurantById(resId: String): ResEntity
}
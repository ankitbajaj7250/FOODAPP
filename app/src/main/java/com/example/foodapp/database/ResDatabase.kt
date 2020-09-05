package com.example.foodapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResEntity::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class ResDatabase : RoomDatabase() {

    abstract fun restaurantDao(): ResDao

    abstract fun orderDao(): OrderDao

}

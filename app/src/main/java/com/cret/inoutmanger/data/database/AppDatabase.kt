package com.cret.inoutmanger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cret.inoutmanger.data.dao.ProductDao
import com.cret.inoutmanger.data.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
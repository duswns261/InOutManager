package com.cret.inoutmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cret.inoutmanager.data.dao.ProductDao
import com.cret.inoutmanager.data.model.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        // 여러 스레드에서 동시에 접근해도 최신 DB 인스턴스를 보장하기 위한 싱글톤 캐시입니다.
        @Volatile
        private var INSTANCE :AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

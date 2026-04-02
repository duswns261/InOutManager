package com.cret.inoutmanger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cret.inoutmanger.data.dao.ProductDao
import com.cret.inoutmanger.data.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
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

// 싱글톤 패턴 적용
// DB 생성을 DB 클래스 내부에 만들어 응집도를 높임
// AppDatabase 클래스의 의도가 명확해짐
// companion object 는 클래스 로딩 시 한 번만 호출되며 앱의 종료까지 유지됩니다.
// 그래서 아래와 같이 작성하고 getDatabase의 Builder 패턴으로 만들어지는 Room Database는 앱 전체에서 공유됩니다.

// Volatile 어노테이션, “다른 스레드에서 최신 값을 볼 수 있도록 가시성을 보장하고, 해당 volatile write/read 사이에 happens-before 관계를 만든다”
// Kotlin 공식 문서도 @Volatile이 붙은 var의 backing field에 대해 read/write가 atomic이고, writes are always made visible to other threads라고 설명합니다.
// 또 그 값을 읽는 스레드는 그 값을 쓰기 전에 일어난 side effects까지 관찰할 수 있다고 설명합니다.
// AppDatabase는 싱글톤으로 다뤄야 하고, 싱글톤 초기화는 한 번만 어긋나도 디버깅 비용이 큽니다.
// 그래서 현재 규모에서는 필수라고 단정할 수 없는 것은 사실이지만, Android/JVM 환경에서 안전한 singleton publication을 보장하는 표준적인 방어 코드로 유지했습니다.
// 결론, "미래를 막연히 대비했다" 라는 과도한 일반화가 아니라, "비용이 적은 안전장치로, 의도가 분명한, 위험도가 높은 초기화 지점에 적용했다"(thead-safe singleton pattern)
// 전역 싱글톤 초기화는 안전하게 닫아두는 편이 더 낫다고 판단했습니다.

// com.cret.inoutmanager.data.database.room_db_singleton_thread_safety_note.txt 내용을 참고해 주세요.
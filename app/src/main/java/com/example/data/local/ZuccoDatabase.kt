package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookingRequestEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ServiceProviderEntity::class,
        BookingRequestEntity::class,
        ReviewEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class ZuccoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceProviderDao(): ServiceProviderDao
    abstract fun bookingDao(): BookingDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: ZuccoDatabase? = null

        fun getDatabase(context: Context): ZuccoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZuccoDatabase::class.java,
                    "zucco_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

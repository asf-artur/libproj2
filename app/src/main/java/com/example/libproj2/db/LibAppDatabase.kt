package com.example.libproj2.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.libproj2.models.User
import com.example.libproj2.models.UserCategory
import java.util.*

@Database(entities = [ User::class ], version = 1)
@TypeConverters(LibAppDatabase.LibAppTypeConverters::class)
abstract class LibAppDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao

    class LibAppTypeConverters{
        @TypeConverter
        fun fromUserCategory(userCategory: UserCategory?): Int?{
            return userCategory?.ordinal
        }

        @TypeConverter
        fun toUserCategory(ordinal: Int?): UserCategory?{
            return UserCategory.values()
                .firstOrNull { c -> c.ordinal == ordinal }
        }

        @TypeConverter
        fun fromCalendar(calendar: Calendar?): Long?{
            return calendar?.time?.time
        }

        @TypeConverter
        fun toCalendar(time: Long?): Calendar?{
            time?.let {
                return Calendar.getInstance()
                    .apply {
                        timeInMillis = it
                    }
            }

            return null
        }
    }
}
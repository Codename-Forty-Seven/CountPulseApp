package com.the.countpulseapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.the.countpulseapp.data.Constants.TABLE_NAME
import com.the.countpulseapp.data.UserInfoPulse

@Database(entities = [UserInfoPulse::class], version = 1)
abstract class Db : RoomDatabase() {

    abstract fun getDao():Dao

    companion object {
        fun getDb(context: Context): Db {
            return Room.databaseBuilder(
                context.applicationContext,
                Db::class.java,
                TABLE_NAME
            ).build()
        }
    }
}
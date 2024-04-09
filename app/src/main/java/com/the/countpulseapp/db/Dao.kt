package com.the.countpulseapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.the.countpulseapp.data.Constants.TABLE_NAME
import com.the.countpulseapp.data.UserInfoPulse
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertUserInfoPulse(userInfoPulse: UserInfoPulse)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getUserInfoPulse(): Flow<List<UserInfoPulse>>
}
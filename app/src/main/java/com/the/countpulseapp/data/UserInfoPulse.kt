package com.the.countpulseapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.the.countpulseapp.data.Constants.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class UserInfoPulse(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "systolic")
    val systolic: Int,
    @ColumnInfo(name = "diastolic")
    val diastolic: Int,
    @ColumnInfo(name = "pulse")
    val pulse: Int,
    @ColumnInfo(name = "dateToSet")
    val dateToSet: String,
    @ColumnInfo(name = "timeToSet")
    val timeToSet: String
)

package com.myroutine.app.data.local.converters

import androidx.room.TypeConverter
import com.myroutine.app.data.local.entity.MeasureType

class Converters {

    @TypeConverter
    fun fromMeasureType(type: MeasureType): String{
        return type.name
    }

    @TypeConverter
    fun toMeasureType(value: String): MeasureType{
        return MeasureType.valueOf(value)
    }
}
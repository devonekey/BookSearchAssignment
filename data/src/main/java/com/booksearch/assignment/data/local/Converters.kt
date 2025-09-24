package com.booksearch.assignment.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

object Converters {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    @JvmStatic
    fun toDate(time: Long?): Date? = time?.let { Date(it) }

    @TypeConverter
    @JvmStatic
    fun fromStringList(list: List<String>?): String? =
        if (list == null) {
            null
        } else {
            gson.toJson(list, listType)
        }

    @TypeConverter
    @JvmStatic
    fun toStringList(json: String?): List<String> =
        if (json.isNullOrBlank()) {
            emptyList()
        } else {
            gson.fromJson(json, listType)
        }
}

package com.kseniabl.tasksapp.db

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import com.kseniabl.tasksapp.models.BookDate
import com.kseniabl.tasksapp.view.TagsModel


object Converters {
    @TypeConverter
    fun toTagsArrayList(value: String): ArrayList<TagsModel> {
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<TagsModel>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromTagsArrayList(list: ArrayList<TagsModel>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<TagsModel>>() {}.type
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toBookDataArrayList(value: String): ArrayList<BookDate> {
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<BookDate>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromBookDateArrayList(list: ArrayList<BookDate>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<BookDate>>() {}.type
        return gson.toJson(list, type)
    }
}
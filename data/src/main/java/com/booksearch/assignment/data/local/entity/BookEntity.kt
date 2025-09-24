package com.booksearch.assignment.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "books",
    indices = [
        Index("title"),
        Index("effectivePrice"),
        Index("datetime")
    ]
)
data class BookEntity(
    @PrimaryKey val isbn: String,
    val title: String,
    val contents: String,
    val url: String,
    val datetime: Date,
    val authors: List<String>,
    val publisher: String,
    val translators: List<String>,
    val price: Int,
    val salePrice: Int,
    val thumbnail: String,
    val status: String,
    val effectivePrice: Int
)

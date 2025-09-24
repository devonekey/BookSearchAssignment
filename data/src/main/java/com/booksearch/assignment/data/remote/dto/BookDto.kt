package com.booksearch.assignment.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BookDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("isbn")
    val isbn: String,
    @SerializedName("datetime")
    val datetime: Date,
    @SerializedName("authors")
    val authors: List<String>,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("translators")
    val translators: List<String>,
    @SerializedName("price")
    val price: Int,
    @SerializedName("sale_price")
    val salePrice: Int,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("status")
    val status: String
)

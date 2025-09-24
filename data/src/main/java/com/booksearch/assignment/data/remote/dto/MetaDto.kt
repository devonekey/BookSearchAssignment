package com.booksearch.assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MetaDto(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)

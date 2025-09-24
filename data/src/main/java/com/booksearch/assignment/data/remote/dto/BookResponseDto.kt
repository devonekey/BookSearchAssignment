package com.booksearch.assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookResponseDto(
    @SerializedName("meta")
    val meta: MetaDto,
    @SerializedName("documents")
    val documents: List<BookDto>
)

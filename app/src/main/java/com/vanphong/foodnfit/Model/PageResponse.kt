package com.vanphong.foodnfit.Model

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,
    val size: Int
)

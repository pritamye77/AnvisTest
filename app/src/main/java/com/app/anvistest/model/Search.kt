package com.app.anvistest.model

import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("Search")
    val resultSearch: List<Movie>?
)
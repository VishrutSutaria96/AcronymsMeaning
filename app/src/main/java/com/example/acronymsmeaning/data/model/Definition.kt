package com.example.acronymsmeaning.data.model

import com.google.gson.annotations.SerializedName

data class Definition(
    @SerializedName("lfs")
    val lfs: List<Lf>,
    @SerializedName("sf")
    val sf: String
)

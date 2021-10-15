package com.example.android.projectsunshine.model

import com.squareup.moshi.Json

data class City (
    @field:Json(name = "name") val cityName : String,
    @field:Json(name = "country") val country : String,
    @field:Json(name = "sunrise") val sunriseUnix : Int,
    @field:Json(name = "sunset") val sunsetUnix : Int
)
package com.example.android.projectsunshine.network

import com.example.android.projectsunshine.model.City
import com.squareup.moshi.Json

data class ApiResponse (
    @field:Json(name = "cod") val cod: String,
    @field:Json(name = "message") val message: Int,
    @field:Json(name = "count") val count: Int,
    @field:Json(name = "list") val list: List<WeatherItem>,
    @field:Json(name = "city") val city: City
)

data class WeatherItem(
    @field:Json(name = "dt") val dt: Int,
    @field:Json(name = "main") val main: Main,
    @field:Json(name = "weather") val weather: List<Weather>,
    @field:Json(name = "dt_txt") val dt_txt: String
)

data class Main (
    @field:Json(name = "temp") val temp: String,
    @field:Json(name = "temp_min") val tempMin: String,
    @field:Json(name = "temp_max") val tempMax: String,
    @field:Json(name = "pressure") val pressure: String,
    @field:Json(name = "humidity") val humidity: String,
)

data class Weather (
    @field:Json(name = "main") val main: String,
    @field:Json(name = "description") val description: String,
)


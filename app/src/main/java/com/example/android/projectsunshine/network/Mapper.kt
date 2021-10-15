package com.example.android.projectsunshine.network

import com.example.android.projectsunshine.model.EntityMapper

class Mapper : EntityMapper<WeatherItem, com.example.android.projectsunshine.model.WeatherItem> {

    override fun mapToModel(entity: WeatherItem): com.example.android.projectsunshine.model.WeatherItem {
        return com.example.android.projectsunshine.model.WeatherItem(
            dateTime = entity.dt,
            temperature = entity.main.temp.toDouble(),
            tempMax = entity.main.tempMax.toDouble(),
            tempMin = entity.main.tempMin.toDouble(),
            pressure = entity.main.pressure.toDouble(),
            humidity = entity.main.humidity.toDouble(),
            weatherText = entity.weather[0].main,
            weatherDesc = entity.weather[0].description
        )
    }

    fun toModelList(list: List<WeatherItem>) : List<com.example.android.projectsunshine.model.WeatherItem>{
        return list.map { mapToModel(it) }
    }
}
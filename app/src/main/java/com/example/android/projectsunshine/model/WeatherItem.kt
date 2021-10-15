package com.example.android.projectsunshine.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

data class WeatherItem (
    val dateTime : Int,
    val temperature : Double,
    val tempMax : Double,
    val tempMin : Double,
    val pressure : Double,
    val humidity : Double,
    val weatherText : String,
    val weatherDesc : String
)

fun WeatherItem.getDayDate() : String {
    val date = Date(dateTime.toLong() * 1000)
    return SimpleDateFormat("EEE, dd MMM, h:mm a").format(date)
}

fun WeatherItem.getDateDay() : String {
    val date = Date(dateTime.toLong() * 1000)
    val sdf1 = SimpleDateFormat("EEEE")
    val sdf2 = SimpleDateFormat("MMM dd")
    val sdf3 = SimpleDateFormat("h:mm a")
    return sdf2.format(date) + "\n" + sdf1.format(date) + "\n" + sdf3.format(date)
}

package com.luojunkai.app.home.homeweather

fun parsehomeweatherFromLiveData(weather: String): homeweather? {
    val weatherInfo = weather.split(",")
    if (weatherInfo.size == 5) {
        return homeweather(
            city = weatherInfo[0],
            weather = weatherInfo[1],
            air = weatherInfo[2],
            airQuality = weatherInfo[3],
            temperature = weatherInfo[4]
        )
    }
    return null
}
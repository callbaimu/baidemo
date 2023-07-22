package com.luojunkai.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luojunkai.app.weather.weather
import com.luojunkai.app.weather.weatherAdapter


class Weather : AppCompatActivity()  {

    private val weatherlist = ArrayList<weather>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)
        initweather()
        val weatherlayoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val weatherrecyclerView: RecyclerView = findViewById(R.id.hourlyTemperature)
        weatherrecyclerView.layoutManager = weatherlayoutManager
        weatherrecyclerView.adapter = weatherAdapter(weatherlist)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initweather(){
        repeat(1){
            weatherlist.add(weather("现在", R.drawable.ic_clear_day, "38℃"))
            weatherlist.add(weather("11时", R.drawable.ic_clear_day, "38℃"))
            weatherlist.add(weather("12时", R.drawable.ic_clear_day, "39℃"))
            weatherlist.add(weather("13时", R.drawable.ic_clear_day, "40℃"))
            weatherlist.add(weather("14时", R.drawable.ic_clear_day, "41℃"))
            weatherlist.add(weather("15时", R.drawable.ic_clear_day, "40℃"))
            weatherlist.add(weather("16时", R.drawable.ic_clear_day, "40℃"))
            weatherlist.add(weather("17时", R.drawable.ic_clear_day, "39℃"))
            weatherlist.add(weather("18时", R.drawable.ic_clear_day, "38℃"))
            weatherlist.add(weather("19时", R.drawable.ic_partly_cloud_day, "36℃"))
            weatherlist.add(weather("20时", R.drawable.ic_partly_cloud_night, "34℃"))
            weatherlist.add(weather("21时", R.drawable.ic_partly_cloud_night, "33℃"))
            weatherlist.add(weather("22时", R.drawable.ic_clear_night, "33℃"))
            weatherlist.add(weather("23时", R.drawable.ic_clear_night, "31℃"))
            weatherlist.add(weather("24时", R.drawable.ic_clear_night, "31℃"))
            weatherlist.add(weather("01时", R.drawable.ic_clear_night, "30℃"))
            weatherlist.add(weather("02时", R.drawable.ic_clear_night, "28℃"))
            weatherlist.add(weather("03时", R.drawable.ic_clear_night, "28℃"))
            weatherlist.add(weather("04时", R.drawable.ic_clear_night, "28℃"))
            weatherlist.add(weather("05时", R.drawable.ic_clear_night, "29℃"))
            weatherlist.add(weather("06时", R.drawable.ic_clear_night, "30℃"))
            weatherlist.add(weather("07时", R.drawable.ic_clear_day, "31℃"))
            weatherlist.add(weather("08时", R.drawable.ic_clear_day, "31℃"))
            weatherlist.add(weather("09时", R.drawable.ic_partly_cloud_day, "32℃"))
            weatherlist.add(weather("10时", R.drawable.ic_partly_cloud_day, "31℃"))
        }
    }
}

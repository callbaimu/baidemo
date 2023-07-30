package com.luojunkai.app.home.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luojunkai.app.R

class weatherAdapter(val weatherlist: List<weather>): RecyclerView.Adapter<weatherAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.w_time)
        val image: ImageView = view.findViewById(R.id.w_image)
        val temperature: TextView = view.findViewById(R.id.w_temperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherlist[position]
        holder.time.text = weather.time
        holder.image.setImageResource(weather.image)
        holder.temperature.text = weather.temperature
    }
}

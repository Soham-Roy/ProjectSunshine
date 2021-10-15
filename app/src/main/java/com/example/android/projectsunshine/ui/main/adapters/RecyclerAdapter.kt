package com.example.android.projectsunshine.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.projectsunshine.R
import com.example.android.projectsunshine.databinding.ForecastItemBinding
import com.example.android.projectsunshine.model.WeatherItem
import com.example.android.projectsunshine.model.getDateDay

class RecyclerAdapter(private val context: Context, private val listener: ItemClickListener)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var list: List<WeatherItem> = mutableListOf()
    private lateinit var tempUnit : String

    inner class ViewHolder(private val binding: ForecastItemBinding)
        : RecyclerView.ViewHolder(binding.root){

            fun bind(position: Int){
                binding.apply {
                    with(list[position]) {
                        dateTv.text = this.getDateDay()
                        weatherIconCard.setImageResource(R.drawable.sunny_weather_icon)
                        temperatureValue.text = temperature.toString()
                        temperatureUnit.text = tempUnit
                        setWeatherIcon(this.weatherText)
                    }
                }
            }

        private fun setWeatherIcon(weatherText : String) {
            when( weatherText ){
                context.getString(R.string.clouds) -> {
                    binding.weatherIconCard.setImageResource(R.drawable.cloudy_weather_icon)
                }
                context.getString(R.string.rain) -> {
                    binding.weatherIconCard.setImageResource(R.drawable.rainy_weather_icon)
                }
                else -> { binding.weatherIconCard.setImageResource(R.drawable.sunny_weather_icon) }
            }
        }
    }

    fun updateData(list: List<WeatherItem>, tempUnit: String){
        this.list = list
        this.tempUnit = tempUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener{
            listener.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int = list.size

}

interface ItemClickListener {
    fun onItemClicked(position: Int)
}
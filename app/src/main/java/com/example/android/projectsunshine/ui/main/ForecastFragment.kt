package com.example.android.projectsunshine.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.projectsunshine.R
import com.example.android.projectsunshine.databinding.FragmentForecastBinding
import com.example.android.projectsunshine.model.WeatherItem
import com.example.android.projectsunshine.model.getDayDate
import com.example.android.projectsunshine.ui.main.adapters.ItemClickListener
import com.example.android.projectsunshine.ui.main.adapters.RecyclerAdapter

class ForecastFragment : Fragment(), ItemClickListener {

    companion object {
        fun newInstance() = ForecastFragment()
    }

    private lateinit var bind : FragmentForecastBinding

    private lateinit var todayWeather : WeatherItem

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var  adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentForecastBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RecyclerAdapter(requireContext(), this)

        bind.recyclerView.adapter = adapter
        bind.viewModel = viewModel

        viewModel.retrofitResponse.observe(viewLifecycleOwner) {
            when (it){
                RetrofitResponseListener.LOADING -> bind.progressCircular.visibility = View.VISIBLE
                RetrofitResponseListener.SUCCESS -> bind.progressCircular.visibility = View.INVISIBLE
                RetrofitResponseListener.FAILURE -> {
                    bind.progressCircular.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.data.observe(viewLifecycleOwner){
            todayWeather = it[0]
            val list : MutableList<WeatherItem> = it as MutableList<WeatherItem>
            list.removeAt(0)
            adapter.updateData(list, viewModel.temperatureUnit.value!!)
        }

        viewModel.today.observe(viewLifecycleOwner) {
            bind.apply {
                temperatureTv.text = it.temperature.toString()
                weatherDesc.text = it.weatherText
                detailsTv.text = getString(R.string.details, it.pressure.toString(), it.humidity.toString())
                todayDate.text = it.getDayDate()
                setWeatherIcon(it.weatherText)
            }
        }

        viewModel.locationLiveData.observe(viewLifecycleOwner) {
            bind.cityTv.text = "${it.cityName}, ${it.country}"
        }

        bind.locationFab.setOnClickListener { openLocation() }
    }

    private fun openLocation() {
        val location : String = viewModel.location
        val uri = Uri.parse("geo:0,0?q=$location")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(intent)
        }catch (e : Exception){
            Toast.makeText(requireContext(), getString(R.string.intent_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun setWeatherIcon(weatherText : String) {
        when( weatherText ){
            getString(R.string.clouds) -> {
                bind.weatherIcon.setImageResource(R.drawable.cloudy_weather_icon)
            }
            getString(R.string.rain) -> {
                bind.weatherIcon.setImageResource(R.drawable.rainy_weather_icon)
            }
            else -> { bind.weatherIcon.setImageResource(R.drawable.sunny_weather_icon) }
        }
    }

    override fun onItemClicked(position: Int) {
        Toast.makeText(requireContext(), "Item at position $position clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.forecast_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.settings_button -> {
                val action = ForecastFragmentDirections.actionForecastFragmentToSettingsFragment()
                findNavController().navigate(action)
                return true
            }
            R.id.refresh_button -> {
                viewModel.makeRetrofitCall()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
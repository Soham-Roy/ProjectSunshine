package com.example.android.projectsunshine.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android.projectsunshine.R
import com.example.android.projectsunshine.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var _bind: MainFragmentBinding
    private val bind get() = _bind

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = MainFragmentBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.today.observe(viewLifecycleOwner) {
            bind.temperatureTv.text = it.temperature.toString()
            setWallpaper(it.weatherText)
            bind.weatherDesc.text = it.weatherText
        }
        viewModel.temperatureUnit.observe(viewLifecycleOwner) {
            bind.temperatureUnitTv.text = it
        }
        viewModel.locationLiveData.observe(viewLifecycleOwner) {
            bind.cityTv.text = "${it.cityName}, ${it.country}"
        }
        bind.mainFab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToForecastFragment()
            findNavController().navigate(action)
        }
    }

    private fun setWallpaper(weatherText: String) {
        when ( weatherText ) {
            getString(R.string.clouds) -> {
                bind.imageView.setImageResource(R.drawable.clouds_wallpaper)
            }
            getString(R.string.rain) -> {
                bind.imageView.setImageResource(R.drawable.rainy_season_wallpaper)
            }
            else -> {
                bind.imageView.setImageResource(R.drawable.sunny_weather_wallpaper)
            }
        }
    }

}
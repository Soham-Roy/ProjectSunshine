package com.example.android.projectsunshine.ui.main

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.android.projectsunshine.R
import com.example.android.projectsunshine.model.City
import com.example.android.projectsunshine.model.WeatherItem
import com.example.android.projectsunshine.network.Mapper
import com.example.android.projectsunshine.network.WeatherApi
import kotlinx.coroutines.launch

const val API_ID = "0f9e437430d1a7c4e4dff267d63a5d3b"
const val UNIT_CELSIUS = "metric"
const val UNIT_FAHRENHEIT = "imperial"
const val UNIT_KELVIN= "standard"

const val CELSIUS_SCALE = " \u2103"
const val FAHRENHEIT_SCALE = " \u2109"
const val KELVIN_SCALE = " K"

enum class RetrofitResponseListener{
    LOADING, SUCCESS, FAILURE
}

class MainViewModel(application: Application) : AndroidViewModel(application),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val _retrofitResponse = MutableLiveData(RetrofitResponseListener.LOADING)
    val retrofitResponse: LiveData<RetrofitResponseListener> get() = _retrofitResponse

    private val _today = MutableLiveData<WeatherItem>()
    val today : LiveData<WeatherItem> get() = _today

    private val _data = MutableLiveData<List<WeatherItem>>()
    val data: LiveData<List<WeatherItem>> get() = _data

    private val _temperatureUnit = MutableLiveData<String>()
    val temperatureUnit: LiveData<String> get() = _temperatureUnit

    var location : String

    private val _locationLiveData = MutableLiveData<City>()
    val locationLiveData : LiveData<City> get() = _locationLiveData

    private val mapper = Mapper()

    private var sharedPref = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    init {
        sharedPref.registerOnSharedPreferenceChangeListener(this)
        location = sharedPref.getString("location", application.getString(R.string.def_location)).toString()
        makeRetrofitCall()
    }

    fun makeRetrofitCall() {
        _retrofitResponse.value = RetrofitResponseListener.LOADING
        setRetrofitCall( sharedPref.getString("unit", "celsius") )
    }

    private fun getRetrofitData(tempUnit : String) {
        _retrofitResponse.value = RetrofitResponseListener.LOADING
        viewModelScope.launch {
            try {
                val response = WeatherApi.retrofitService.getData(
                    location,
                    API_ID,
                    tempUnit
                )
                _data.value = mapper.toModelList(response.list)
                _today.value = mapper.mapToModel(response.list[0])
                _retrofitResponse.value = RetrofitResponseListener.SUCCESS
                _locationLiveData.value = response.city
            } catch (e : Exception){
                _retrofitResponse.value = RetrofitResponseListener.FAILURE
                Log.e("ViewModel", e.toString())
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if ( key.equals("unit") ) {
            setRetrofitCall( sharedPreferences?.getString("unit", "celsius") )
        }
        else if ( key.equals("location") ){
            location = sharedPref.getString("location", location).toString()
            makeRetrofitCall()
        }
    }

    private fun setRetrofitCall(s : String?){
        when (s) {
            "celsius" -> {
                getRetrofitData(UNIT_CELSIUS)
                _temperatureUnit.value = CELSIUS_SCALE
            }
            "fahrenheit" -> {
                getRetrofitData(UNIT_FAHRENHEIT)
                _temperatureUnit.value = FAHRENHEIT_SCALE
            }
            "kelvin" -> {
                getRetrofitData(UNIT_KELVIN)
                _temperatureUnit.value = KELVIN_SCALE
            }
        }
    }

    fun getWeatherItemAtPos(position : Int) : WeatherItem {
        return _data.value?.get(position)!!
    }

    override fun onCleared() {
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
        super.onCleared()
    }
}
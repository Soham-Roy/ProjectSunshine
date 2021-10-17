package com.example.android.projectsunshine.ui.main

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.android.projectsunshine.R
import com.example.android.projectsunshine.databinding.FragmentStartBinding
import java.security.Permission
import java.util.*
import java.util.function.Consumer

class StartFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener,
    LocationListener {

    val REQUEST_CODE = 1

    private var _bind : FragmentStartBinding?= null
    private val bind get() = _bind!!

    private val viewModel : MainViewModel by activityViewModels()
    private var locationString : String ?= null

    private lateinit var pref : SharedPreferences

    private lateinit var locationManager : LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _bind = FragmentStartBinding.inflate(layoutInflater)
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        locationString = pref.getString(getString(R.string.key_location), null)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ( locationString != null ) {
            openViewModel()
            return
        }
        getLocation()
    }

    private fun openViewModel() {
        Handler().postDelayed({
            viewModel.retrofitResponse.observe(viewLifecycleOwner) {
                when (it) {
                    RetrofitResponseListener.LOADING -> loadingUI()
                    RetrofitResponseListener.SUCCESS -> {
                        val action = StartFragmentDirections.actionStartFragmentToMainFragment()
                        findNavController().navigate(action)
                    }
                    RetrofitResponseListener.FAILURE -> errorUI()
                }
            }
        }, 2000)
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
                val permission = mutableListOf<String>()
                permission.add(Manifest.permission.ACCESS_FINE_LOCATION)
                permission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                requireActivity().requestPermissions(permission.toTypedArray(), REQUEST_CODE)

            return
        }

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ) {
            locationManager.getCurrentLocation(
                LocationManager.NETWORK_PROVIDER,
                null, requireActivity().application.mainExecutor,
                Consumer {
                    getCityCountry(it)
                }
            )
        }
        else {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000, 0F, this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        getCityCountry(location)
        locationManager.removeUpdates(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when ( requestCode ) {
            REQUEST_CODE -> {
                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    getLocation()
                }
            }
        }
    }

    private fun getCityCountry(location: Location) {
        val geocoder  = Geocoder(requireContext(), Locale.getDefault())
        val list : List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val cityName : String = list[0].locality
        val countryName : String = list[0].countryName
        locationString = "$cityName, $countryName"

        with( pref.edit() ) {
            putString(getString(R.string.key_location), locationString)
        }
    }

    private fun loadingUI() {
        bind.circularProgressIndicator.visibility = View.VISIBLE
        bind.errorIcon.visibility = View.INVISIBLE
        bind.errorText.visibility = View.INVISIBLE
    }

    private fun errorUI() {
        bind.circularProgressIndicator.visibility = View.INVISIBLE
        bind.errorIcon.visibility = View.VISIBLE
        bind.errorText.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        openViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.forecast_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when ( item.itemId ) {
            R.id.settings_button -> {
                val action = StartFragmentDirections.actionStartFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
            R.id.refresh_button -> {
                viewModel.makeRetrofitCall()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
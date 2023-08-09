package com.example.locationsample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.locationsample.databinding.MainActivityBinding
import com.example.locationsample.ui.map.MapFragment
import com.example.locationsample.ui.map.map_command.MapCommands
import com.example.locationsample.utils.location.LocationUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainAppViewModel by viewModels()

    private val binding: MainActivityBinding by lazy{
        MainActivityBinding.inflate(LayoutInflater.from(this))
    }

    @Inject
    lateinit var locationUtil: LocationUtil

    private val TAG = "MainActivity"

    private val mapFragment: MapFragment by lazy {
        MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initMap()
    }

    private fun initMap(){
        supportFragmentManager
            .beginTransaction()
            .add(binding.mapContainer.id, mapFragment)
            .commit()
    }
}
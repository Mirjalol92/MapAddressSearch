package com.example.locationsample.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.locationsample.R
import com.example.locationsample.base.BaseFragment
import com.example.locationsample.databinding.FragmentMainBinding
import com.example.locationsample.ui.map.map_command.Commands
import com.example.locationsample.ui.map.map_command.MapCommands
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate){
    private val TAG = "MainFragment"

    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setUpLayout()
        viewLifecycleOwner.lifecycleScope.launch{
            setObservers()
        }
    }
    private fun setUpLayout(){}
    private suspend fun setObservers(){
        viewLifecycleOwner.whenStarted {
            viewModel.mapPointingAddress.collect{
                it?.let {
                    binding.tvTitle.text = "${it.subLocality} ${it.thoroughfare} ${it.subThoroughfare}"
                    binding.tvSubTitle.text = it.getAddressLine(0)
                }
            }
        }
    }

    private fun setOnClickListeners(){
        binding.addressLL.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }
        binding.fabMyLocation.setOnClickListener {
            viewModel.locateMe()
        }
        binding.tvAddressSearch.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }
    }
}
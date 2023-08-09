package com.example.locationsample.ui.search

import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.locationsample.base.BaseFragment
import com.example.locationsample.databinding.FragmentSearchAddressBinding
import com.example.locationsample.ui.search.adapter.AddressAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchAddressBinding>(FragmentSearchAddressBinding::inflate){

    private val viewModel: SearchViewModel by viewModels()

    val adapter = AddressAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpEditText()
        setUpRv()
        startCollecting()
    }
    private fun startCollecting(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addressList.collect {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun setUpRv(){
        binding.rvAddress.adapter = adapter
    }
    private fun setOnClickListener(){
        binding.searchIv.setOnClickListener {
            viewModel.performSearch()
        }
        binding.etSearch.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_SEARCH){
                viewModel.performSearch()
                true
            }else
                false
        }

    }
    private fun setUpEditText(){
        binding.etSearch.doAfterTextChanged {
            viewModel.setSearchKeyword(it.toString())
        }
        binding.etSearch.requestFocus()
    }
}

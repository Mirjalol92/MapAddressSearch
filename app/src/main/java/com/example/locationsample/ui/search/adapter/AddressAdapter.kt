package com.example.locationsample.ui.search.adapter

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsample.databinding.ItemAddressBinding

class AddressAdapter: ListAdapter<Address, AddressAdapter.AddressViewHolder>(object:DiffUtil.ItemCallback<Address>(){
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.getAddressLine(0) == newItem.getAddressLine(0)
    }
    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.getAddressLine(0) == newItem.getAddressLine(0)
    }

}){
    inner class AddressViewHolder(private val binding: ItemAddressBinding): RecyclerView.ViewHolder(binding.root){
        fun display(item: Address){
            binding.tvAddressTitle.text = "${item.subLocality ?: ""} ${item.thoroughfare ?: ""} ${item.subThoroughfare ?: ""}"
            binding.tvAddressSubTitle.text = item.getAddressLine(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.display(currentList[position])
    }
}
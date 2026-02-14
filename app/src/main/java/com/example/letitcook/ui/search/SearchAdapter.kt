package com.example.letitcook.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letitcook.data.Restaurant
import com.example.letitcook.databinding.ItemRestaurantBinding

class SearchAdapter(private val restaurants: List<Restaurant>) : RecyclerView.Adapter<SearchAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val res = restaurants[position]
        with(holder.binding) {
            tvResName.text = res.name
            tvResDetails.text = res.type
            tvResRating.text = res.rating

            // טעינת תמונה
            Glide.with(root.context).load(res.imageUrl).centerCrop().into(ivResImage)
        }
    }

    override fun getItemCount() = restaurants.size
}
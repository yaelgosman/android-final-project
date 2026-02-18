package com.example.letitcook.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letitcook.R
import com.example.letitcook.databinding.ItemRestaurantBinding
import com.example.letitcook.models.YelpRestaurant

class SearchAdapter(
    private var restaurants: List<YelpRestaurant>,
    private val onItemClick: (YelpRestaurant) -> Unit
) : RecyclerView.Adapter<SearchAdapter.RestaurantViewHolder>() {

    fun updateList(newlist: List<YelpRestaurant>) {
        restaurants = newlist
        notifyDataSetChanged()
    }

    class RestaurantViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val res = restaurants[position]
        with(holder.binding) {
            tvResName.text = res.name

            // Get category (e.g., "Italian")
            val type = res.categories?.firstOrNull()?.title ?: "Restaurant"
            val city = res.location?.city ?: ""
            tvResDetails.text = "$type • $city"

            tvResRating.text = res.rating.toString()

            // Load Image
            if (!res.imageUrl.isNullOrEmpty()) {
                Glide.with(root.context)
                    .load(res.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background) // Add a placeholder drawable if you have one
                    .into(ivResImage)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(res)
        }
    }

    override fun getItemCount() = restaurants.size
}
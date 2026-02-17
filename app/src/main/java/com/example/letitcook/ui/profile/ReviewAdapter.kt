package com.example.letitcook.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.letitcook.R
import com.example.letitcook.databinding.ItemReviewBinding
import com.example.letitcook.models.entity.Post
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter : ListAdapter<Post, ReviewAdapter.ReviewViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            // 1. Set Restaurant Name
            binding.tvRestaurantName.text = post.location // if (post.location.isNotEmpty()) post.location else "Unknown Place"

            // 2. Set Content & Rating
            binding.tvReviewContent.text = post.description
            binding.rbReviewRating.rating = post.rating

            // 3. Set Date
            val date = Date(post.timestamp)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvReviewDate.text = format.format(date)

            // 4. Load Image (Only if URL exists)
            if (!post.postImageUrl.isNullOrEmpty()) {
                binding.ivReviewImage.visibility = View.VISIBLE
                Picasso.get()
                    .load(post.postImageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.darker_gray) // Make sure this color exists in colors.xml
                    .into(binding.ivReviewImage)
            } else {
                // Collapse the image view if there is no image
                binding.ivReviewImage.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }
}
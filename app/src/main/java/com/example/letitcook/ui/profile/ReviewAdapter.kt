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

enum class PostAction {
    EDIT, DELETE
}

class ReviewAdapter(
    private val onActionClicked: (Post, PostAction) -> Unit
) : ListAdapter<Post, ReviewAdapter.ReviewViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position), onActionClicked)
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, onActionClicked: (Post, PostAction) -> Unit) {
            // Set Restaurant Name
            binding.tvRestaurantName.text = post.location // if (post.location.isNotEmpty()) post.location else "Unknown Place"

            // Set Content & Rating
            binding.tvReviewContent.text = post.description
            binding.rbReviewRating.rating = post.rating

            // Set Date
            val date = Date(post.timestamp)
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvReviewDate.text = format.format(date)

            // Load Image (Only if URL exists)
            if (!post.postImageUrl.isNullOrEmpty()) {
                binding.ivReviewImage.visibility = View.VISIBLE
                Picasso.get()
                    .load(post.postImageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.darker_gray)
                    .into(binding.ivReviewImage)
            } else {
                // Collapse the image view if there is no image
                binding.ivReviewImage.visibility = View.GONE
            }

            // Handle menu click
            binding.btnMenuOptions.setOnClickListener { view ->
                showPopupMenu(view, post, onActionClicked)
            }
        }

        private fun showPopupMenu(view: View, post: Post, onAction: (Post, PostAction) -> Unit) {
            val popup = android.widget.PopupMenu(view.context, view)
            // Inflate menu or add items programmatically
            popup.menu.add(0, 1, 0, "Edit")
            popup.menu.add(0, 2, 0, "Delete")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> onAction(post, PostAction.EDIT)
                    2 -> onAction(post, PostAction.DELETE)
                }
                true
            }
            popup.show()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }
}
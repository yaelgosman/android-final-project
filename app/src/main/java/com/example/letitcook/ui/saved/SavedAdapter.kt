package com.example.letitcook.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letitcook.R
import com.example.letitcook.databinding.ItemSavedPostBinding
import com.example.letitcook.models.entity.Post

class SavedAdapter(
    private val onBookmarkClick: (Post) -> Unit
) : RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    private var posts: List<Post> = emptyList()

    fun setPosts(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemSavedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvTitle.text = post.userName // Using userName as Restaurant name
            tvSubtitle.text = post.location
            tvRating.text = post.rating.toString()

            // Logic for "Open Now" vs Time.
            // Since we don't have hours in DB, we'll fake it based on Rating for demo or static
            tvStatusBadge.text = "Open Now"

            if (!post.postImageUrl.isNullOrEmpty()) {
                Glide.with(root.context)
                    .load(post.postImageUrl)
                    .centerCrop()
                    .into(ivPostImage)
            }

            // Bookmark Click
            ivBookmark.setOnClickListener {
                onBookmarkClick(post)
            }
        }
    }

    class SavedViewHolder(val binding: ItemSavedPostBinding) : RecyclerView.ViewHolder(binding.root)
}
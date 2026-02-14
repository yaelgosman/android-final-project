package com.example.letitcook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letitcook.R
import com.example.letitcook.data.local.entity.PostEntity
import com.example.letitcook.databinding.ItemPostBinding

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var posts: List<PostEntity> = emptyList()

    fun setPosts(newPosts: List<PostEntity>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvUserName.text = post.userName
            tvLocation.text = post.restaurantName
            tvDescription.text = post.description
            tvRating.text = String.format("%.1f / 10", post.rating)

            if (post.postImageUrl.isNotEmpty()) {
                Glide.with(root.context)
                    .load(post.postImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background) // temporal profile image while loading
                    .into(ivPostImage)
            }

            if (post.userAvatarUrl.isNotEmpty()) {
                Glide.with(root.context)
                    .load(post.userAvatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(ivAvatar)
            }
        }
    }
}
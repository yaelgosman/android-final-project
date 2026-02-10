package com.example.letitcook.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letitcook.databinding.ItemPostBinding
import com.example.letitcook.model.Post
import com.example.letitcook.R

class PostsAdapter(
    private val posts: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        with(holder.binding) {
            tvUserName.text = post.userName
            tvLocation.text = post.location
            tvRating.text = "${post.rating} / 10"
            tvDescription.text = post.description

//            Glide.with(root)
//                .load(post.userAvatarUrl)
//                .placeholder(R.drawable.ic_avatar_placeholder)
//                .into(ivAvatar)
//
//            Glide.with(root)
//                .load(post.postImageUrl)
//                .placeholder(R.drawable.ic_post_placeholder)
//                .into(ivPostImage)
        }
    }

    override fun getItemCount(): Int = posts.size
}

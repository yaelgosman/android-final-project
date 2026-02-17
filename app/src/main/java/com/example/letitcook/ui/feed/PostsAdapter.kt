package com.example.letitcook.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letitcook.databinding.ItemPostBinding
import com.example.letitcook.models.entity.Post
import com.example.letitcook.R
import com.example.letitcook.utils.ImageUtils
import android.view.View

class PostsAdapter(
    private val posts: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var postList: List<Post> = posts

    class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        with(holder.binding) {
            tvUserName.text = post.userName
            tvLocation.text = post.location
            tvRating.text = "${post.rating} / 5"
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

        // Handles the image loading
        if (post.postImageUrl != null) {
            holder.binding.ivPostImage.visibility = View.VISIBLE

            val isLocalUri = post.postImageUrl.startsWith("content://") //Checks if its a local image from the gallery or from the web
            var request = com.squareup.picasso.Picasso.get().load(post.postImageUrl)

            // If local, apply the rotation fix using your Utils
            if (isLocalUri) {
                var uri = android.net.Uri.parse(post.postImageUrl)
                val rotation = ImageUtils.getRotationAngle(holder.itemView.context, uri)
                request = request.rotate((rotation))
            }

            // Load with standard settings
            request
                .fit()
                .centerCrop()
                .placeholder(R.drawable.bg_dashed_border) // Show placeholder while loading
                .into(holder.binding.ivPostImage)

        } else {
                // Hide image view if the post has no photo
//                holder.binding.ivPostImage.visibility = View.GONE
        }

        // Handles the user pfp
        if (!post.userAvatarUrl.isNullOrEmpty()) {
            holder.binding.ivAvatar.visibility = View.VISIBLE

            val isLocalUri = post.userAvatarUrl.startsWith("content://") //Checks if its a local image from the gallery or from the web
            var request = com.squareup.picasso.Picasso.get().load(post.userAvatarUrl)

            // If local, apply the rotation fix using your Utils
            if (isLocalUri) {
                var uri = android.net.Uri.parse(post.userAvatarUrl)
                val rotation = ImageUtils.getRotationAngle(holder.itemView.context, uri)
                request = request.rotate((rotation))
            }

            // Load with standard settings
            request
                .fit()
                .centerCrop()
                .into(holder.binding.ivAvatar)

        } else {
            // Hide image view if the post has no photo
//                holder.binding.ivAvatar.visibility = View.GONE

        }
    }

    fun updatePosts(newPosts: List<Post>) {
        this.postList = newPosts
        notifyDataSetChanged() // Tells the recycler to redraw itself with real data
    }

    override fun getItemCount(): Int = postList.size
}

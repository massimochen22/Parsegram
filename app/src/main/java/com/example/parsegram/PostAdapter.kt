package com.example.parsegram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.TimeFormatter

class PostAdapter(val context: Context, val posts: ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvUsername:TextView
        val ivImage: ImageView
        val tvDescription: TextView
        val tvCreatedAt: TextView
        init{
            tvUsername = itemView.findViewById(R.id.tvUserName)
            ivImage= itemView.findViewById(R.id.ivImage)
            tvDescription = itemView.findViewById(R.id.tvDescription)
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt)
        }

        fun bind(post:Post){
            tvDescription.text = post.getDescription()
            tvUsername.text =post.getUser()?.username
            tvCreatedAt.text = TimeFormatter.getTimeDifference(post.createdAt.toString())

            Glide.with(itemView.context).load(post.getImage()?.url).into(ivImage)
        }

    }

}
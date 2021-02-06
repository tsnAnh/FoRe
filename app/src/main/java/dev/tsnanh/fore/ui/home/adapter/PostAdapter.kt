package dev.tsnanh.fore.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.tsnanh.fore.Post
import dev.tsnanh.fore.databinding.ItemPostBinding
import dev.tsnanh.fore.util.recyclerview.OnItemClickListener

class PostAdapter(
    private val listener: OnItemClickListener<Post>
) : ListAdapter<Post, PostViewHolder>(PostDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder.from(parent)

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

}

class PostViewHolder private constructor(
    private val binding: ItemPostBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPostBinding.inflate(inflater, parent, false)
            return PostViewHolder(binding)
        }
    }

    fun bind(post: Post, listener: OnItemClickListener<Post>) {
        with(binding) {
            photo.load(post.photos[0].data)
            title.text = post.postTitle
            textView4.text = post.address
            root.setOnClickListener {
                listener.onItemClick(post)
            }
        }
    }
}

class PostDiffUtil : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.postId == newItem.postId
    }
}
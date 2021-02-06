package dev.tsnanh.fore.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.tsnanh.fore.databinding.ItemPhotoBinding

class PhotoAdapter : ListAdapter<Pair<String, String>, PhotoViewHolder>(PairStringUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder.from(parent)

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PairStringUtil : DiffUtil.ItemCallback<Pair<String, String>>() {
    override fun areItemsTheSame(
        oldItem: Pair<String, String>,
        newItem: Pair<String, String>,
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: Pair<String, String>,
        newItem: Pair<String, String>,
    ): Boolean {
        return oldItem.second == newItem.second
    }
}

class PhotoViewHolder private constructor(
    private val binding: ItemPhotoBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): PhotoViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPhotoBinding.inflate(inflater, parent, false)
            return PhotoViewHolder(binding)
        }
    }

    fun bind(pair: Pair<String, String>) {
        with(binding) {
            photos.load(pair.first)
            title.text = pair.second
        }
    }
}


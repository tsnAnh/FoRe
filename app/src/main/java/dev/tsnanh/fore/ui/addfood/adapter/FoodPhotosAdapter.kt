package dev.tsnanh.fore.ui.addfood.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.tsnanh.fore.databinding.ItemPhotoBinding
import dev.tsnanh.fore.ml.FoodModel
import dev.tsnanh.fore.util.extension.toBitmap
import dev.tsnanh.fore.util.recyclerview.OnItemClickListener
import org.tensorflow.lite.support.image.TensorImage

class FoodPhotosAdapter(
    private val model: FoodModel,
    private val listener: OnItemClickListener<Uri> = OnItemClickListener {  },
) : ListAdapter<Pair<String, Uri>, PhotoViewHolder>(UriDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder.from(parent)

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position).second, model)
    }

}

class UriDiffUtil : DiffUtil.ItemCallback<Pair<String, Uri>>() {
    override fun areItemsTheSame(oldItem: Pair<String, Uri>, newItem: Pair<String, Uri>): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Pair<String, Uri>, newItem: Pair<String, Uri>): Boolean {
        return oldItem.second.path == newItem.second.path
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

    fun bind(uri: Uri, model: FoodModel) {
        val probability =
            model.process(TensorImage.fromBitmap(uri.toBitmap(binding.root.context))).probabilityAsCategoryList
        val predictedTitle = probability.maxByOrNull { it.score }?.label ?: "Ko biet"
        with(binding) {
            photos.load(uri)
            title.text = predictedTitle
        }
    }
}


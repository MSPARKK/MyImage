package com.mspark.myimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mspark.myimage.data.ImageData
import com.mspark.myimage.databinding.ItemImageBinding

class ImageAdapter: ListAdapter<ImageData, ImageAdapter.ImageViewHolder>(COMPARATOR) {
    var onClickLike: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding) {
            onClickLike?.invoke(it)
        }
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<ImageData>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<ImageData>() {
            override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean =
                oldItem.isMyImage == newItem.isMyImage
                        && oldItem.dateTime == newItem.dateTime

            override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean =
                oldItem.thumbnailUrl == newItem.thumbnailUrl
        }
    }

    class ImageViewHolder(
        private val binding: ItemImageBinding,
        private val onClickLike: ((Int) -> Unit)? = null
    ): RecyclerView.ViewHolder(binding.root), CustomClickListener {
        fun bind(imageData: ImageData) {
            binding.model = imageData
            binding.likeClickListener = this
        }

        override fun likeClicked() {
            onClickLike?.invoke(layoutPosition)
        }
    }
}


interface CustomClickListener {
    fun likeClicked()
}
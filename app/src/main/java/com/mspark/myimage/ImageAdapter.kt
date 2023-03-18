package com.mspark.myimage

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.databinding.ItemImageBinding

class ImageAdapter: ListAdapter<KakaoImage, ImageAdapter.ImageViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, parent.context)
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val sampleBackgroundSubject = getItem(position)
        holder.bind(sampleBackgroundSubject, position)
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<KakaoImage>() {
            override fun areContentsTheSame(oldItem: KakaoImage, newItem: KakaoImage): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: KakaoImage, newItem: KakaoImage): Boolean =
                oldItem == newItem
        }
    }

    class ImageViewHolder(
        private val binding: ItemImageBinding,
        private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(kakaoImage: KakaoImage, position: Int) {

            Glide.with(context)
                .load(kakaoImage.thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.itemImage)

//            binding.itemTimeStamp.text = kakaoImage.dateTime

            @RequiresApi(Build.VERSION_CODES.O)
            binding.itemTimeStamp.text = kakaoImage.getTimeStamp()
        }
    }
}
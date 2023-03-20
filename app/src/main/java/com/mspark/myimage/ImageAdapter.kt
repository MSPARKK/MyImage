package com.mspark.myimage

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mspark.myimage.data.KakaoImage
import com.mspark.myimage.databinding.ItemImageBinding
import com.mspark.myimage.util.Constants.Fragment.MY_IMAGE
import com.mspark.myimage.util.Constants.Fragment.SEARCH

class ImageAdapter(private val type: String): ListAdapter<KakaoImage, ImageAdapter.ImageViewHolder>(COMPARATOR) {
    var onClickLike: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, parent.context, type) {
            onClickLike?.invoke(it)
        }
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<KakaoImage>?) {
        super.submitList(list?.let { ArrayList(it) })
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
        private val context: Context,
        private val type: String,
        private val onClickLike: ((Int) -> Unit)? = null
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(kakaoImage: KakaoImage) {
            binding.itemLikeImg.setOnClickListener {
                onClickLike?.invoke(layoutPosition)
            }

            Glide.with(context)
                .load(kakaoImage.thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.itemImage)

            if (kakaoImage.isMyImage) {
                binding.itemLikeImg.setImageResource(R.drawable.icon_favorite)
            } else {
                binding.itemLikeImg.setImageResource(R.drawable.icon_favorite_border)
            }

            setItemTimeStamp(kakaoImage)
        }

        private fun setItemTimeStamp(kakaoImage: KakaoImage) {
            if (type == MY_IMAGE) return

            binding.itemTimeStamp.text = kakaoImage.getTimeStamp()

            // @@ 잠깐! todo : test 로직 삭제
            if (kakaoImage.url != null) {
                binding.itemTimeStamp.setTextColor(ContextCompat.getColorStateList(context, R.color.purple_500))
            } else {
                binding.itemTimeStamp.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
            }
        }

        init {
            binding.itemTimeStamp.isVisible = type == SEARCH
        }
    }


}
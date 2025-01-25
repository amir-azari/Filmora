package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemPosterBinding
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer

class PosterAdapter  :
    RecyclerView.Adapter<PosterAdapter.ViewHolder>() {

    private var onItemClickListener: ((ResponseImage.Poster) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseImage.Poster) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemPosterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: ResponseImage.Poster) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.filePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.filePath
                }

                imgPoster.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true,
                    imgShimmerContainer
                )



            }
            // Click
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ResponseImage.Poster>) {
        val limitedList = if (list.size > 10) list.subList(0, 10) else list
        differ.submitList(limitedList)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in differ.currentList.indices) {
            holder.bind(differ.currentList[position])
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseImage.Poster>() {
        override fun areItemsTheSame(oldItem: ResponseImage.Poster, newItem: ResponseImage.Poster): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: ResponseImage.Poster, newItem: ResponseImage.Poster): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}
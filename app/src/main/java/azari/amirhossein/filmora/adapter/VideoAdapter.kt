package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemSimilarRemommendationBinding
import azari.amirhossein.filmora.databinding.ItemVideoBinding
import azari.amirhossein.filmora.models.detail.ResponseImage
import azari.amirhossein.filmora.models.detail.ResponseTvSimilar
import azari.amirhossein.filmora.models.detail.ResponseVideo
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.loadImageWithoutShimmer
import azari.amirhossein.filmora.utils.toFormattedDate

class VideoAdapter  :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var onItemClickListener: ((ResponseVideo.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseVideo.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgVideo.scaleType

        fun bind(item: ResponseVideo.Result) {
            binding.apply {
                binding.txtTitle.text = item.name ?: Constants.Defaults.NOT_APPLICABLE
                binding.txtPublishedAt.text = item.publishedAt?.substring(0, 10)?.toFormattedDate() ?: Constants.Message.UNKNOWN_ERROR

                val thumbnailUrl = "https://img.youtube.com/vi/${item.key}/hqdefault.jpg"
                if (!item.key.isNullOrEmpty()){
                    binding.imgPlay.visibility = View.VISIBLE
                }
                binding.imgVideo.loadImageWithShimmer(
                    path = thumbnailUrl,
                    fallback = R.drawable.film,
                    errorRes = R.drawable.film_slash,
                    originalScaleType = originalScaleType,
                    hasStroke = true,
                    imgShimmerContainer
                )
                if (item.site == "YouTube") {
                    binding.imgYoutubeLogo.visibility = View.VISIBLE
                } else {
                    binding.imgYoutubeLogo.visibility = View.GONE
                }
            }
            // Click
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ResponseVideo.Result>) {
        val limitedList = if (list.size > 10) list.subList(0, 10) else list
        differ.submitList(limitedList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in differ.currentList.indices) {
            holder.bind(differ.currentList[position])

        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseVideo.Result>() {
        override fun areItemsTheSame(oldItem: ResponseVideo.Result, newItem: ResponseVideo.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseVideo.Result, newItem: ResponseVideo.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}